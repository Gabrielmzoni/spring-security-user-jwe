package br.com.gmsoft.userjwe.dto;

public class View {
    private View() {
        throw new IllegalStateException("Utility class");
    }

    public interface PutVisibility { }
    public interface PostVisibility extends PutVisibility { }
    public interface GetVisibility extends PostVisibility { }
}
