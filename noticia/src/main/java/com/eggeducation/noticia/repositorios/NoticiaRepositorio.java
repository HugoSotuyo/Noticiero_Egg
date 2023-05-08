/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eggeducation.noticia.repositorios;

import com.eggeducation.noticia.entidades.Noticia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author hdsot
 */
@Repository
public interface NoticiaRepositorio extends JpaRepository<Noticia,String> {
    @Query("SELECT n FROM Noticia n WHERE n.titulo=:titulo")
    public Noticia buscarPorTitulo(@Param("titulo") String titulo);
    

    List<Noticia> findByOrderByAltaDesc();

    public Noticia getOne(String id);

    public Optional<Noticia> findById(String id);

}


