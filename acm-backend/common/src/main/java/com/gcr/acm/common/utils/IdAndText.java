package com.gcr.acm.common.utils;

/**
 * Contains an id and a text.
 *
 * @author Razvan Dani
 */
public class IdAndText {
    private Integer id;
    private String text;

    public IdAndText(Integer id, String text) {
        this.id = id;
        this.text = text;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdAndText idAndText = (IdAndText) o;

        if (id != null ? !id.equals(idAndText.id) : idAndText.id != null) return false;
        return text != null ? text.equals(idAndText.text) : idAndText.text == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}
