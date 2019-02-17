package ru.javaops.basejava.webapp.storage.serializer;

import ru.javaops.basejava.webapp.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Xml stream serialization strategy
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-17
 */
public class DataStreamSerializer implements StreamSerializer {
    @Override
    public Void doWrite(Resume r, OutputStream os) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(os)) {
            dos.writeUTF(r.getUuid());
            dos.writeUTF(r.getFullName());
            Map<ContactType, String> contacts = r.getContacts();
            dos.writeInt(contacts.size());
            for (Map.Entry<ContactType, String> entry : contacts.entrySet()) {
                dos.writeUTF(entry.getKey().name());
                dos.writeUTF(entry.getValue());
            }
            Map<SectionType, Section> sections = r.getSections();
            dos.writeInt(sections.size());
            for (Map.Entry<SectionType, Section> entry : sections.entrySet()) {
                writeSection(entry, dos);
            }
        }
        return null;
    }

    @Override
    public Resume doRead(InputStream is) throws IOException {
        Resume result;
        try (DataInputStream dis = new DataInputStream(is)) {
            result = new Resume(dis.readUTF(), dis.readUTF());
            int contactsSize = dis.readInt();
            for (int i = 0; i < contactsSize; i++) {
                result.addContact(ContactType.valueOf(dis.readUTF()), dis.readUTF());
            }
            int sectionSize = dis.readInt();
            for (int i = 0; i < sectionSize; i++) {

                SectionType type = SectionType.valueOf(dis.readUTF());
                result.addSection(type, readSection(dis, type));
            }

        }
        return result;
    }

    private Section readSection(DataInputStream dis, SectionType type) throws IOException {
        Section result = null;
        switch (type) {
            case OBJECTIVE:
            case PERSONAL:
                result = readTextSection(dis);
                break;
            case ACHIEVEMENT:
            case QUALIFICATIONS:
                result = readListSection(dis);
                break;
            case EXPERIENCE:
            case EDUCATION:
                result = readOrganizationSection(dis);
        }
        return result;

    }

    private void writeSection(Map.Entry<SectionType, Section> section, DataOutputStream dos) throws IOException {
        SectionType key = section.getKey();
        dos.writeUTF(key.name());
        switch (key) {
            case OBJECTIVE:
            case PERSONAL:
                writeTextSection((TextSection) section.getValue(), dos);
                break;
            case ACHIEVEMENT:
            case QUALIFICATIONS:
                writeListSection((ListSection) section.getValue(), dos);
                break;
            case EXPERIENCE:
            case EDUCATION:
                writeOrganizationSection((OrganizationSection) section.getValue(), dos);
        }
    }

    private void writeTextSection(TextSection section, DataOutputStream dos) throws IOException {
        dos.writeUTF(section.toString());
    }

    private void writeListSection(ListSection section, DataOutputStream dos) throws IOException {
        List<String> items = section.getItems();
        dos.writeInt(items.size());
        for (String item : items) {
            dos.writeUTF(item);
        }
    }

    private void writeOrganizationSection(OrganizationSection section, DataOutputStream dos) throws IOException {
        List<Organization> organizations = section.getOrganizations();
        dos.writeInt(organizations.size());
        for (Organization organization : organizations) {
            writeOrganization(organization, dos);
        }
    }

    private Section readTextSection(DataInputStream dis) throws IOException {
        return new TextSection(dis.readUTF());
    }

    private Section readListSection(DataInputStream dis) throws IOException {
        List<String> items = new ArrayList<>();
        int size = dis.readInt();
        for (int i = 0; i < size; i++) {
            items.add(dis.readUTF());
        }
        return new ListSection(items);
    }

    private Section readOrganizationSection(DataInputStream dis) throws IOException {
        List<Organization> organizations = new ArrayList<>();
        int size = dis.readInt();
        for (int i = 0; i < size; i++) {
            organizations.add(readOrganization(dis));
        }
        return new OrganizationSection(organizations);
    }

    private Organization readOrganization(DataInputStream dis) throws IOException {
        Link homePage = new Link(dis.readUTF(), readNullable(dis));
        List<Organization.Position> positions = new ArrayList<>();
        int size = dis.readInt();
        for (int i = 0; i < size; i++) {
            positions.add(readPosition(dis));
        }
        return new Organization(homePage, positions);
    }

    private Organization.Position readPosition(DataInputStream dis) throws IOException {
        return new Organization.Position(
                LocalDate.parse(dis.readUTF()),
                LocalDate.parse(dis.readUTF()),
                dis.readUTF(),
                readNullable(dis)
        );
    }

    private void writeOrganization(Organization organization, DataOutputStream dos) throws IOException {
        Link homePage = organization.getHomePage();
        dos.writeUTF(homePage.getName());
        writeNullable(homePage.getUrl(), dos);
        List<Organization.Position> positions = organization.getPositions();
        dos.writeInt(positions.size());
        for (Organization.Position position : positions) {
            writePosition(position, dos);
        }
    }

    private void writePosition(Organization.Position position, DataOutputStream dos) throws IOException {
        dos.writeUTF(position.getStartDate().toString());
        dos.writeUTF(position.getEndDate().toString());
        dos.writeUTF(position.getTitle());
        writeNullable(position.getDescription(), dos);
    }

    private void writeNullable(String line, DataOutputStream dos) throws IOException {
        if (line != null) {
            dos.writeBoolean(true);
            dos.writeUTF(line);
        } else {
            dos.writeBoolean(false);
        }
    }

    private String readNullable(DataInputStream dis) throws IOException {
        String result = null;
        if (dis.readBoolean()) {
            result = dis.readUTF();
        }
        return result;
    }
}