package com.kakaopay.todolist.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

public class TodoDTO {

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class RequestBase {
        @JsonProperty("content")
        private String content;

        @JsonProperty("parent_id")
        private Integer parentId;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode(callSuper = false)
    public static class CreateRequest extends RequestBase{
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode(callSuper = false)
    public static class UpdateRequest extends RequestBase {
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class ResponseBase {
        @JsonProperty("id")
        private Integer id;

        @JsonProperty("content")
        private String content;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode(callSuper = false)
    public static class CreateResponse extends ResponseBase {
        @JsonProperty("created_at")
        private Date createdAt;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode(callSuper = false)
    public static class UpdateResponse extends ResponseBase {
        @JsonProperty("updated_at")
        private Date updatedAt;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode(callSuper = false)
    public static class SearchResponseBase extends ResponseBase {
        @JsonProperty("created_at")
        private Date createdAt;

        @JsonProperty("updated_at")
        private Date updatedAt;

        @JsonProperty("completed_at")
        private Date completedAt;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode(callSuper = false)
    public static class FindOneResponse extends SearchResponseBase {
        @JsonProperty("display_content")
        private String displayContent;

        @JsonProperty("ancestors")
        private List<TreePathDTO> ancestors;

        @JsonProperty("descendants")
        private List<TreePathDTO> descendants;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @EqualsAndHashCode(callSuper = false)
    public static class FindAllResponse extends SearchResponseBase {
        @JsonProperty("display_content")
        private String displayContent;
    }
}
