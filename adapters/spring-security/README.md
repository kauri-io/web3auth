

Anonymous
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            log.info("################### {}", (AnonymousAuthenticationToken) authentication);
        }
        if (authentication instanceof IdentifiedAuthenticationToken) {
            log.info("################### {}", (IdentifiedAuthenticationToken) authentication);
        }
        
authenticated      

        if (principal instanceof IdentifiedAuthenticationToken) {
            log.info("################### authenticated");
            log.info("################### {}", (IdentifiedAuthenticationToken) principal);
        }