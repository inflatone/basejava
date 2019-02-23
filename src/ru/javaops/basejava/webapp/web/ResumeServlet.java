package ru.javaops.basejava.webapp.web;

import ru.javaops.basejava.webapp.Config;
import ru.javaops.basejava.webapp.model.*;
import ru.javaops.basejava.webapp.storage.Storage;
import ru.javaops.basejava.webapp.util.DateUtil;
import ru.javaops.basejava.webapp.util.HtmlUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResumeServlet extends HttpServlet {
    private Storage storage = Config.get().getStorage();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String uuid = request.getParameter("uuid");
        String fullName = request.getParameter("fullName");
        final Resume r = storage.get(uuid);
        r.setFullName(fullName);
        for (ContactType type : ContactType.values()) {
            String value = request.getParameter(type.name());
            if (HtmlUtil.isEmpty(value)) {
                r.getContacts().remove(type);
            } else {
                r.setContact(type, value);
            }
        }
        for (SectionType type : SectionType.values()) {
            String data = request.getParameter(type.name());
            String[] values = request.getParameterValues(type.name());
            if (HtmlUtil.isEmpty(data) && values.length < 2) {
                r.getSections().remove(type);
            } else {
                switch (type) {
                    case OBJECTIVE:
                    case PERSONAL:
                        r.setSection(type, new TextSection(data));
                        break;
                    case ACHIEVEMENT:
                    case QUALIFICATIONS:
                        r.setSection(type, new ListSection(data.split("\\n")));
                        break;
                    case EXPERIENCE:
                    case EDUCATION:
                        r.setSection(type, new OrganizationSection(grabOrganizations(request, type, values)));
                        break;
                }
            }
        }
        storage.update(r);
        response.sendRedirect("resume");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uuid = request.getParameter("uuid");
        String action = request.getParameter("action");
        if (action == null) {
            request.setAttribute("resumes", storage.getAllSorted());
            request.getRequestDispatcher("/WEB-INF/jsp/list.jsp").forward(request, response);
            return;
        }
        Resume r;
        switch (action) {
            case "delete":
                storage.delete(uuid);
                response.sendRedirect("resume");
                return;
            case "view":
                r = storage.get(uuid);
                break;
            case "edit":
                r = storage.get(uuid);
                addFirstEmptyOrganizationSections(r);
                fillEmptySections(r);
                break;
            default:
                throw new IllegalArgumentException("Action " + action + " is illegal");
        }
        request.setAttribute("resume", r);
        request.getRequestDispatcher(
                ("view".equals(action) ? "WEB-INF/jsp/view.jsp" : "WEB-INF/jsp/edit.jsp")
        ).forward(request, response);
    }

    private void addFirstEmptyOrganizationSections(Resume r) {
        for (SectionType type : new SectionType[]{SectionType.EXPERIENCE, SectionType.EDUCATION}) {
            Section section = new OrganizationSection(
                    getEmptyFirstOrganizations((OrganizationSection) r.getSection(type))
            );
            r.setSection(type, section);
        }
    }

    private void fillEmptySections(Resume r) {
        for (SectionType type : SectionType.values()) {
            if (r.getSections().get(type) == null) {
                r.setSection(type, Resume.EMPTY.getSection(type));
            }
        }
    }

    private List<Organization> grabOrganizations(HttpServletRequest request, SectionType type, String[] names) {
        List<Organization> organizations = new ArrayList<>();
        String[] urls = request.getParameterValues(type.name() + "url");
        for (int i = 0; i < names.length; i++) {
            if (!HtmlUtil.isEmpty(names[i])) {
                List<Organization.Position> positions = grabPositions(request, names[i], type.name() + i);
                organizations.add(new Organization(new Link(names[i], urls[i]), positions));
            }
        }
        return organizations;
    }

    private List<Organization.Position> grabPositions(HttpServletRequest request, String name, String prefix) {
        List<Organization.Position> positions = new ArrayList<>();
        if (!HtmlUtil.isEmpty(name)) {
            String[] startDates = getValues(request, prefix, "startDate");
            String[] endDates = getValues(request, prefix, "endDate");
            String[] titles = getValues(request, prefix, "title");
            String[] descriptions = getValues(request, prefix, "description");
            for (int j = 0; j < titles.length; j++) {
                if (!HtmlUtil.isEmpty(titles[j])) {
                    positions.add(new Organization.Position(
                            DateUtil.parse(startDates[j]), DateUtil.parse(endDates[j]), titles[j], descriptions[j]
                    ));
                }
            }
        }
        return positions;
    }

    private String[] getValues(HttpServletRequest request, String prefix, String name) {
        return request.getParameterValues(prefix + name);
    }

    private List<Organization> getEmptyFirstOrganizations(OrganizationSection section) {
        List<Organization> result = new ArrayList<>();
        result.add(Organization.EMPTY);
        if (section != null) {
            for (Organization org : section.getOrganizations()) {
                List<Organization.Position> emptyFirstPositions = new ArrayList<>();
                emptyFirstPositions.add(Organization.Position.EMPTY);
                emptyFirstPositions.addAll(org.getPositions());
                result.add(new Organization(org.getHomePage(), emptyFirstPositions));
            }
        }
        return result;
    }
}
