package com.shake_art.back.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/errors")
class ExceptionTestController {

    @GetMapping("/business")
    public void business() {
        throw new BusinessException("BUSINESS_RULE", "Regle metier violee");
    }

    @GetMapping("/not-found")
    public void notFound() {
        throw new ResourceNotFoundException("Ressource introuvable");
    }

    @GetMapping("/forbidden")
    public void forbidden() {
        throw new AccessDeniedException("Interdit");
    }

    @GetMapping("/unexpected")
    public void unexpected() {
        throw new RuntimeException("Boom");
    }

    @PostMapping("/validation")
    public void validation(@Valid @RequestBody ValidationBody body) {
        // No-op
    }

    static class ValidationBody {
        @NotBlank(message = "Le nom est obligatoire")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
