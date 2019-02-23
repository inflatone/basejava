package ru.javaops.basejava.webapp.storage.serializer;

import ru.javaops.basejava.webapp.model.*;
import ru.javaops.basejava.webapp.util.DateUtil;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
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
            writeCollection(dos, r.getContacts().entrySet(), entry -> writeContact(entry, dos));
            writeCollection(dos, r.getSections().entrySet(), entry -> writeSection(entry, dos));
        }
        return null;
    }

    @Override
    public Resume doRead(InputStream is) throws IOException {
        Resume result;
        try (DataInputStream dis = new DataInputStream(is)) {
            result = new Resume(dis.readUTF(), dis.readUTF());
            readElements(dis, () -> readContact(dis, result));
            readElements(dis, () -> readSection(dis, result));
        }
        return result;
    }

    private void readContact(DataInputStream dis, Resume resume) throws IOException {
        resume.setContact(ContactType.valueOf(dis.readUTF()), dis.readUTF());
    }

    private void writeContact(Map.Entry<ContactType, String> contact, DataOutputStream dos) throws IOException {
        dos.writeUTF(contact.getKey().name());
        dos.writeUTF(contact.getValue());
    }

    private void readSection(DataInputStream dis, Resume resume) throws IOException {
        SectionType type = SectionType.valueOf(dis.readUTF());
        Section result = null;
        switch (type) {
            case OBJECTIVE:
            case PERSONAL:
                result = new TextSection(dis.readUTF());
                break;
            case ACHIEVEMENT:
            case QUALIFICATIONS:
                result = new ListSection(readList(dis, dis::readUTF));
                break;
            case EXPERIENCE:
            case EDUCATION:
                result = new OrganizationSection(readList(dis, () -> readOrganization(dis)));
        }
        resume.setSection(type, result);
    }

    private void writeSection(Map.Entry<SectionType, Section> section, DataOutputStream dos) throws IOException {
        SectionType key = section.getKey();
        dos.writeUTF(key.name());
        switch (key) {
            case OBJECTIVE:
            case PERSONAL:
                dos.writeUTF(((TextSection) section.getValue()).getContent());
                break;
            case ACHIEVEMENT:
            case QUALIFICATIONS:
                writeCollection(dos, ((ListSection) section.getValue()).getItems(), dos::writeUTF);
                break;
            case EXPERIENCE:
            case EDUCATION:
                writeCollection(
                        dos,
                        ((OrganizationSection) section.getValue()).getOrganizations(),
                        org -> writeOrganization(org, dos));
        }
    }

    private <T> List<T> readList(DataInputStream dis, ElementReader<T> reader) throws IOException {
        int size = dis.readInt();
        List<T> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(reader.read());
        }
        return result;
    }

    private Organization readOrganization(DataInputStream dis) throws IOException {
        return new Organization(
                new Link(dis.readUTF(), readNullable(dis)),
                readList(dis, () -> readPosition(dis))
        );
    }

    private Organization.Position readPosition(DataInputStream dis) throws IOException {
        return new Organization.Position(
                readLocalDate(dis),
                readLocalDate(dis),
                dis.readUTF(),
                readNullable(dis)
        );
    }

    private void writeOrganization(Organization organization, DataOutputStream dos) throws IOException {
        Link homePage = organization.getHomePage();
        dos.writeUTF(homePage.getName());
        writeNullable(homePage.getUrl(), dos);
        writeCollection(dos, organization.getPositions(), pos -> writePosition(pos, dos));
    }

    private void writePosition(Organization.Position position, DataOutputStream dos) throws IOException {
        writeLocalDate(position.getStartDate(), dos);
        writeLocalDate(position.getEndDate(), dos);
        dos.writeUTF(position.getTitle());
        writeNullable(position.getDescription(), dos);
    }

    private LocalDate readLocalDate(DataInputStream dis) throws IOException {
        return DateUtil.of(dis.readInt(), dis.readInt());
    }

    private void writeLocalDate(LocalDate date, DataOutputStream dos) throws IOException {
        dos.writeInt(date.getYear());
        dos.writeInt(date.getMonthValue());
    }

    private String readNullable(DataInputStream dis) throws IOException {
        String result = null;
        if (dis.readBoolean()) {
            result = dis.readUTF();
        }
        return result;
    }

    private void writeNullable(String line, DataOutputStream dos) throws IOException {
        if (line != null) {
            dos.writeBoolean(true);
            dos.writeUTF(line);
        } else {
            dos.writeBoolean(false);
        }
    }

    private void readElements(DataInputStream dis, ElementProcessor reader) throws IOException {
        int size = dis.readInt();
        for (int i = 0; i < size; i++) {
            reader.process();
        }
    }

    private <T> void writeCollection(DataOutputStream dos, Collection<T> collection, ElementWriter<T> writer) throws IOException {
        dos.writeInt(collection.size());
        for (T item : collection) {
            writer.write(item);
        }
    }

    private interface ElementProcessor {
        void process() throws IOException;
    }

    private interface ElementReader<T> {
        T read() throws IOException;
    }

    private interface ElementWriter<T> {
        void write(T t) throws IOException;
    }
}