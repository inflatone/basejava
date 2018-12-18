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
        Arrays.fill(this.storage, 0, this.size, null);
        this.size = 0;
    }

    /**
     * Appends the specified resume to the end of the storage.
     *
     * @param resume resume to be appended to this storage
     */
    void save(Resume resume) {
        this.storage[this.size++] = resume;
    }

    /**
     * Returns the resume with the specified uuid
     *
     * @param uuid unique number of the resume
     * @return the resume which uuid equals specified one,
     * or <tt>null</tt> if there was no resume for <tt>uuid</tt>
     */
    Resume get(String uuid) {
        int index = this.indexOf(uuid);
        return index != -1 ? this.storage[index] : null;
    }

    /**
     * Removes the resume <tt>uuid</tt> of which equals the specified one.
     *
     * @param uuid unique number of the resume
     */
    void delete(String uuid) {
        int index = this.indexOf(uuid);
        if (index != -1) {
            System.arraycopy(
                    this.storage,
                    index + 1,
                    this.storage,
                    index,
                    --this.size - index
            );
        }
    }

    /**
     * @return array, contains only Resumes in storage (without null)
     */
    Resume[] getAll() {
        return Arrays.copyOf(this.storage, this.size);
    }

    /**
     * @return the number of resumes this storage contains
     */
    int size() {
        return this.size;
    }

    /**
     * Returns the index of the specified Resume <tt>uuid</tt>  of which equals the specified one.
     *
     * @param uuid unique number of the resume
     * @return index of the specified resume
     */
    private int indexOf(String uuid) {
        return IntStream.range(0, this.size)
                .filter(i -> this.storage[i].toString().equals(uuid))
                .findAny()
                .orElse(-1);
    }
}
