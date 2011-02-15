package ru.csv

/**
 * Reads {@link ru.beans.Bean}s from csv file.
 *
 * Should:
 *  - write beans as is to .csv file
 *  - write beans subset of fields
 *  - write beans fields in particular order
 *  - write beans using specific convertors (e.g. date convertors)
 *
 *  - detect when beans don't match header
 *  - nice writer: adds empty string
 *  - strict writer: fail fast
 *
 *  - append to existing .csv file (i.e. read file header and write beans accordingly)
 *
 * User: dima
 * Date: 15/2/11
 */
class CsvWriter {
}
