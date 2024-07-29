package com.fooddelivery.customer.infrastructure.exception.http;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;

@Getter
public class HttpConflictException {
    @Getter
    private static class LinkInfo {
        @Schema(description = "The relation of the resource", example = "self")
        @Setter
        private LinkRelation rel;

        @Schema(description = "The URL of the resource", example = "{host}/api/v1/customers/1")
        @Setter
        private String href;
    }

    @Schema(description = "Details about the relation")
    private LinkInfo link;

    @Schema(example = "User with email john@example.com is already created.")
    @Setter
    private String message;

    public HttpConflictException(String message) {
        this.message = message;
    }

    public void setLink(Link link) {
        LinkInfo linkInfo = new LinkInfo();
        linkInfo.setHref(link.getHref());
        linkInfo.setRel(link.getRel());
        this.link = linkInfo;
    }
}