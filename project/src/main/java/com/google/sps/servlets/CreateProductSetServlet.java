package com.google.sps.servlets;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;

import com.google.sps.data.ServletLibrary;
import com.google.sps.data.ProductSetEntity;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/createProductSet")
public class CreateProductSetServlet extends HttpServlet {

  protected DatastoreService datastore;
  protected Gson gson;

  public CreateProductSetServlet() {
    super();
    datastore = DatastoreServiceFactory.getDatastoreService();
    gson = new Gson();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String productSetDisplayName = request.getParameter("productSetDisplayName");
    // Only add the product set if doesn't already exist in the database.
    Entity result = ServletLibrary.checkProductSetExists(datastore, productSetDisplayName);
    if (result == null) {
      String productSetId = ServletLibrary.generateUUID();
      Entity productSet = new Entity("ProductSet", productSetId);
      productSet.setProperty("productSetId", productSetId);
      productSet.setProperty("productSetDisplayName", productSetDisplayName);
      datastore.put(productSet);
    }

    // Product search api function goes here.


    response.sendRedirect("/businessAccount.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Return all product set display names from the database.
    List<ProductSetEntity> productSets = ServletLibrary.listAllProductSets(datastore);
    List<String> productSetNames = new ArrayList<>();
    for (ProductSetEntity productSet : productSets) {
      productSetNames.add(productSet.getProductSetDisplayName());
    }

    String json = gson.toJson(productSetNames);

    // Send the JSON as the response.
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}