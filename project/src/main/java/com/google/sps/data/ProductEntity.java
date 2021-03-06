package com.google.sps.data;
import java.util.ArrayList;
import java.util.List;

public class ProductEntity {
  private String productId;
  private String productDisplayName;
  private String productSetId;
  private String productCategory;
  private String businessId;
  private float price; 
  private List<String> gcsUrls; 
  private List<String> imageUrls; 
  private List<String> labels; 
  private String productDescription;
  private String cloudVisionAnnotation;

  public ProductEntity(String productId,
                       String productDisplayName,
                       String productSetId,
                       String productCategory,
                       String businessId,
                       float price,
                       List<String> gcsUrls,
                       List<String> imageUrls,
                       List<String> labels,
                       String productDescription,
                       String cloudVisionAnnotation) {
    this.productId = productId;
    this.productDisplayName = productDisplayName;
    this.productSetId = productSetId;
    this.productCategory = productCategory;
    this.businessId = businessId;
    this.price = price;
    this.gcsUrls = gcsUrls;
    this.imageUrls = imageUrls;
    this.labels = labels;
    this.productDescription = productDescription;
    this.cloudVisionAnnotation = cloudVisionAnnotation;
  }

  // we only really care about the productId when comparing two given labels.
  @Override
  public int hashCode() {
    return productId.hashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ProductEntity other = (ProductEntity) obj;
    return (productId.equals(other.getProductId()));
  }

  public String getProductId() {
    return productId;
  }

  public String getProductSetId() {
    return productSetId;
  }

  public String getBusinessId() {
    return businessId;
  }

  public String getProductCategory() {
    return productCategory;
  }

  public List<String> getLabels() {
    return labels;
  }

  public List<String> getGcsUrls(){
      return gcsUrls;
  }
}