package com.example.niemawidaha.deck_practice.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * POJO for a single card
 */
public class CardModel {

    // member fields:
    @SerializedName("suit")
    @Expose
    private String suit;

    @SerializedName("images")
    @Expose
    private Images images;

    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("value")
    @Expose
    private String value;

    public String getSuit() {
        return suit;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CardModel{" +
                "code='" + code + '\'' +
                '}';
    }

    public class Images {

        @SerializedName("png")
        @Expose
        private String png;

        @SerializedName("svg")
        @Expose
        private String svg;

        public String getPng() {
            return png;
        }

        public void setPng(String png) {
            this.png = png;
        }

        public String getSvg() {
            return svg;
        }

        public void setSvg(String svg) {
            this.svg = svg;
        }

        @Override
        public String toString() {
            return "Images{" +
                    "png='" + png + '\'' +
                    ", svg='" + svg + '\'' +
                    '}';
        }
    } // ends images inner class

}
