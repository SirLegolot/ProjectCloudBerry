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
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.FileInfo;

import com.google.sps.data.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet("/viewProducts")
public class ViewProductsServlet extends HttpServlet {

  protected DatastoreService datastore;
  protected BlobstoreService blobstore;
  protected Gson gson;
  protected UserService userService;

  public ViewProductsServlet() {
    super();
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
    blobstore = BlobstoreServiceFactory.getBlobstoreService();
    gson = new Gson();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: product search.

    // Retrieve parameters from the request
    String productSetDisplayName = request.getParameter("productSetDisplayName");
    String productCategory = request.getParameter("productCategory");
    String businessId = request.getParameter("businessId");
    String sortOrder = request.getParameter("sortOrder");
    String searchId = request.getParameter("searchId");

    // Set parameters to apprpriate defaults, if necessary.
    if (businessId.equals("getFromDatabase")) {
      businessId = userService.getCurrentUser().getUserId();
    }
    if (productCategory.equals("none")) {
      productCategory = null;
    }
    String productSetId = null;
    ProductSetEntity productSet = null;
    if (!productSetDisplayName.equals("none")) {
      // true indicates we are searching with the displayname instead of the id.
      productSet = ServletLibrary.retrieveProductSetInfo(datastore, productSetDisplayName, true);
    }
    if (productSet != null) {
      productSetId = productSet.getProductSetId();
    }

    // Search database based on the filters. 
    List<ProductEntity> products = 
      ServletLibrary.findProducts(datastore, 
                                  businessId,
                                  productSetId, 
                                  productCategory, 
                                  sortOrder);

    if (searchId != null) {
      SearchInfo searchInfo = ServletLibrary.retrieveSearchInfo(datastore, searchId);

      if (searchInfo.getGcsUrl() != null) {
        
        String generalProductSetId = "cloudberryAllProducts";
        List <String> productSearchIds = ProductSearchLibrary.getSimilarProductsGcs(generalProductSetId, 
                                            searchInfo.getProductCategory(), changeGcsFormat(searchInfo.getGcsUrl()));
        List<ProductEntity> imageSearchProducts = new ArrayList<>();
        productSearchIds.forEach(productId->imageSearchProducts.add(ServletLibrary.retrieveProductInfo(datastore, productId)));

        Set<ProductEntity> uniqueProducts = new HashSet<>(products);
        List<ProductEntity> productsDisplayed = new ArrayList<>();
        for (ProductEntity product : imageSearchProducts) {
          if (uniqueProducts.contains(product)) productsDisplayed.add(product);
        }
        products = productsDisplayed;
      }

      // Text query if it is specified, will take in this list and output a new
      // list that satisfies the query.
      if (searchInfo.getTextSearch() != null) {
        products = TextSearchLibrary.textSearch(datastore, products, 
                                                searchInfo.getTextSearch());
      }
    }

    // Send the response.
    String json = gson.toJson(products);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String textSearch = request.getParameter("textSearch");
    boolean userUploadedImage = Boolean.parseBoolean(request.getParameter("userUploadedImage"));

    // Creates a new SearchInfo object, which will be stored in datastore.
    String searchId = ServletLibrary.generateUUID();
    Entity searchInfo = new Entity("SearchInfo", searchId);
    searchInfo.setProperty("searchId", searchId);
    searchInfo.setProperty("timestamp", System.currentTimeMillis());
    if (userService.isUserLoggedIn()) {
      searchInfo.setProperty("userId", userService.getCurrentUser().getUserId());
    } else {
      searchInfo.setProperty("userId", null);
    }
    
    // Checks if the user sent a text search or a image search or both. Adds
    // query properties appropriately.
    searchInfo.setProperty("gcsUrl", null);
    searchInfo.setProperty("imageUrl", null);
    searchInfo.setProperty("textSearch", null);
    searchInfo.setProperty("productCategory", null);    
    if (userUploadedImage) {
      String gcsUrl = CloudStorageLibrary.getGcsFilePath(request, blobstore);
      BlobKey blobKey = blobstore.createGsBlobKey(gcsUrl);
      String imageUrl = "/serveBlobstoreImage?blobKey=" + blobKey.getKeyString();
      searchInfo.setProperty("gcsUrl", gcsUrl);
      searchInfo.setProperty("imageUrl", imageUrl);
      searchInfo.setProperty("productCategory", 
        request.getParameter("productCategorySearch"));
    } 
    if (!textSearch.isEmpty()) {
      searchInfo.setProperty("textSearch", textSearch);
    }
    datastore.put(searchInfo);
  
    response.sendRedirect("/viewProducts.html?searchId="+searchId);
  }

  private String changeGcsFormat(String gcsUri){
    
    String newGcsFormat = "gs://";
    
    String[] gcsArray = gcsUri.split("/");

    newGcsFormat += gcsArray[2] + "/" + gcsArray[3];
    // The last and the penultimate indexes of the split gcsUri give the strings required to reformat the 
    // gcsuri to a valid parameter for the createReferenceImage method.

    return newGcsFormat;
  }
}