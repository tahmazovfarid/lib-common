package az.ailab.lib.common.model.vo;

import lombok.Data;

@Data
public class Document {

    private String request;
    private String requestDescription;
    private String successResponse;
    private String successResponseDescription;
    private String failResponse;
    private String failResponseDescription;

}
