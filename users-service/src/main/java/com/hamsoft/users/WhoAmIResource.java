package com.hamsoft.users;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import static java.util.Objects.requireNonNull;

@Path("/whoami")
public class WhoAmIResource {

    private final Template whoami;

    private final SecurityContext securityContext;

    public WhoAmIResource(Template whoami, SecurityContext securityContext) {
        this.whoami = requireNonNull(whoami, "page is required");
        this.securityContext = securityContext;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        var userId = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : null;
        return whoami.data("name", userId);
    }

}
