@Test
    void refreshEmiteNuevoTokenParaUsuarioActivo() {
        RefreshRequest req = new RefreshRequest();
        req.setToken("token-viejo");

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("ana@duoc.cl");
        when(jwtService.validar("token-viejo")).thenReturn(claims);
        when(repository.findByCorreoIgnoreCase("ana@duoc.cl")).thenReturn(Optional.of(usuario(true)));
        when(jwtService.generarToken(any())).thenReturn("nuevo");

        assertEquals("nuevo", service.refresh(req).getToken());
    }