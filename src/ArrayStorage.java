import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Array based storage for Resumes
 */
public class ArrayStorage {
    /**
     * The array buffer into which the resumes are stored.
     */
    private Resume[] storage = new Resume[10000];

    /**
     * The number of resumes this storage contains.
     */
    private int size;

    /**
     * Removes all of the resumes from this storage.
     */
    void clear() {
        Arrays.fill(storage, 0, size, null);
        size = 0;
    }

    /**
     * Appends the specified resume to the end of the storage.
     *
     * @param resume resume to be appended to this storage
     */
    void save(Resume resume) {
        storage[size++] = resume;
    }

    /**
     * Returns the resume with the specified uuid
     *
     * @param uuid unique number of the resume
     * @return the resume which uuid equals specified one,
     * or <tt>null</tt> if there was no resume for <tt>uuid</tt>
     */
    Resume get(String uuid) {
        int index = indexOf(uuid);
        return index != -1 ? storage[index] : null;
    }

    /**
     * Removes the resume <tt>uuid</tt> of which equals the specified one.
     *
     * @param uuid unique number of the resume
     */
    void delete(String uuid) {
        int index = indexOf(uuid);
        if (index != -1) {
            size--;
            System.arraycopy(
                    storage,
                    index + 1,
                    storage,
                    index,
                    size - index
            );
        }
    }

    /**
     * @return array, contains only Resumes in storage (without null)
     */
    Resume[] getAll() {
        return Arrays.copyOf(storage, size);
    }

    /**
     * @return the number of resumes this storage contains
     */
    int size() {
        return size;
    }

    /**
     * Returns the index of the specified Resume <tt>uuid</tt>  of which equals the specified one.
     *
     * @param uuid unique number of the resume
     * @return index of the specified resume
     */
    private int indexOf(String uuid) {
        return IntStream.range(0, size)
                .filter(i -> storage[i].uuid.equals(uuid))
                .findAny()
                .orElse(-1);
    }
}
