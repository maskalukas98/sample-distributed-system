package com.fooddelivery.commerce.infrastructure.exception.partner;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;

@Getter
public class PartnerConflictHttpException {
    @Getter
    private static class LinkInfo {
        @Schema(description = "The relation of the resource", example = "self")
        @Setter
        private LinkRelation rel;

        @Schema(description = "The URL of the resource", example = "{host}/api/v1/partners/268435457")
        @Setter
        private String href;
    }

    @Schema(description = "Details about the relation")
    private LinkInfo link;

    @Schema(example = "Partner with provided company name 'Fast pizza delivery' already exists.")
    @Setter
    private String message;

    public PartnerConflictHttpException(String companyName) {
        this.message = "Partner with provided company name '" + companyName + "' already exists.";;
    }

    public void setLink(Link link) {
        LinkInfo linkInfo = new LinkInfo();
        linkInfo.setHref(link.getHref());
        linkInfo.setRel(link.getRel());
        this.link = linkInfo;
    }
}