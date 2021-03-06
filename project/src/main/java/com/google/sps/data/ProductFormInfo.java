package com.google.sps.data;
import java.util.List;

public class ProductFormInfo {
  private CloudVisionAnnotation annotation;
  private List<String> labels;
  private String description;
  private String gcsUrl;
  private String imageUrl;

  public ProductFormInfo(CloudVisionAnnotation annotation, 
                         List<String> labels, 
                         String description, 
                         String gcsUrl, 
                         String imageUrl) {
    this.annotation = annotation;
    this.labels = labels;
    this.description = description;
    this.gcsUrl = gcsUrl;
    this.imageUrl = imageUrl;
  }
}