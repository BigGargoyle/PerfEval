package cz.cuni.mff.d3s.perfeval.evaluation;

import cz.cuni.mff.d3s.perfeval.resultdatabase.FileWithResultsData;

import java.util.*;

/**
 * Collection of results of comparison of two sets of samples and their versions
 */
public class MeasurementComparisonResultCollection implements Collection<MeasurementComparisonRecord> {
    /**
     * 2D array of file with results of performance tests metadata
     * First dimension is for versions of performance tests
     * Second dimension is for files with results of performance tests of one version
     */
    FileWithResultsData[][] originalFilesMetadata;
    /**
     * List of results of comparison of two sets of samples
     *
     * @see MeasurementComparisonRecord
     */
    List<MeasurementComparisonRecord> records;

    /**
     * Constructor for MeasurementComparisonResultCollection
     *
     * @param originalFiles 2D array of file metadata that are compared
     */
    public MeasurementComparisonResultCollection(FileWithResultsData[][] originalFiles) {
        records = new ArrayList<>();
        this.originalFilesMetadata = originalFiles;
        assert originalFiles.length == 2;
    }

    /**
     * Getter for old version of performance tests
     *
     * @return old version of performance tests
     */
    public String getOldVersion() {
        return originalFilesMetadata[0][0].version().commitVersionHash();
    }

    /**
     * Getter for new version of performance tests
     *
     * @return new version of performance tests
     */
    public String getNewVersion() {
        return originalFilesMetadata[1][0].version().commitVersionHash();
    }

    @Override
    public int size() {
        return records.size();
    }

    @Override
    public boolean isEmpty() {
        return records.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return records.contains(o);
    }

    @Override
    public Iterator<MeasurementComparisonRecord> iterator() {
        return records.iterator();
    }

    @Override
    public Object[] toArray() {
        return records.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return records.toArray(a);
    }

    public void sort(Comparator<MeasurementComparisonRecord> cmp) {
        records.sort(cmp);
    }


    @Override
    public boolean add(MeasurementComparisonRecord measurementComparisonRecord) {
        return records.add(measurementComparisonRecord);
    }

    @Override
    public boolean remove(Object o) {
        return records.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new HashSet<>(records).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends MeasurementComparisonRecord> c) {
        return records.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return records.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return records.retainAll(c);
    }

    @Override
    public void clear() {
        records.clear();
    }

    public MeasurementComparisonRecord get(int i) {
        return records.get(i);
    }
}
