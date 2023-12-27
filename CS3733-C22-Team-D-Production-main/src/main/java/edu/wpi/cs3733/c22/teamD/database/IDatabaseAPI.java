package edu.wpi.cs3733.c22.teamD.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface IDatabaseAPI<T, G> {

  ArrayList<T> getAll();

  void addNewEntity(T entity) throws SQLException;

  void deleteTuple(G primaryKey) throws SQLException;

  void storeToCSV(File filename) throws FileNotFoundException;

  void restoreFromCSV(String filename) throws IOException, SQLException;

  void refresh();

  void modifyEntity(G ID, String field, Object newField) throws SQLException;

  void tableOnStartup(String filename);

  boolean createTable() throws SQLException;

  void populateFromList() throws SQLException;

  void uploadNewConnection() throws SQLException;

  void clearTable() throws SQLException;
}
