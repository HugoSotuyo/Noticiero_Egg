/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eggeducation.noticia.servicios;

import com.eggeducation.noticia.entidades.Noticia;
import com.eggeducation.noticia.excepciones.MiException;
import com.eggeducation.noticia.repositorios.NoticiaRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.hibernate.annotations.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author hdsot
 */

@Service
public class NoticiaServicio {
    
    @Autowired
    private NoticiaRepositorio noticiaRepositorio;
    
    @Transactional
    public void crearNoticia(String titulo,String seccion,String cuerpo)throws MiException{
        validar(titulo,seccion, cuerpo);
        
        Noticia noticia=new Noticia();
        noticia.setTitulo(titulo);
        noticia.setCuerpo(cuerpo);
        noticia.setSeleccion(seccion);
        noticia.setAlta(new Date());
        
        noticiaRepositorio.save(noticia);
    }
    public List<Noticia> listarNoticia(){
        List<Noticia> noticias = new ArrayList();
        
        noticias=noticiaRepositorio.findByOrderByAltaDesc();
        return noticias;
    }
    @Transactional
    public Noticia getOne(String id){
        return noticiaRepositorio.getOne(id);
    }
    
    @Transactional
    public Noticia mostraNoticia(String titulo){
        Noticia noticia = new Noticia();
        
        noticia=noticiaRepositorio.buscarPorTitulo(titulo);
        
        return noticia;
    }
    
    @Transactional
    public void modificarNoticia( String id,String titulo,String seleccion, String cuerpo)throws MiException{
        validar(titulo,seleccion,cuerpo);
                
        Optional<Noticia>respuesta=noticiaRepositorio.findById(id);
        
        if(respuesta.isPresent()){
            Noticia noticia=respuesta.get();
            noticia.setTitulo(titulo);
            noticia.setCuerpo(cuerpo);
            noticia.setSeleccion(seleccion);
            noticiaRepositorio.save(noticia);
        }
    }
    private void validar(String titulo,String seleccion,String cuerpo) throws MiException{
        
        if(titulo==null||titulo.isEmpty()){
            throw new MiException("El titulo no pude ser nulo ni estar vacio");
        }
        if(seleccion==null||seleccion.isEmpty()){
            throw new MiException("La seleccion no pude ser nulo ni estar vacio");
        }
        if(cuerpo==null||cuerpo.isEmpty()){
            throw new MiException("El cuerpo no pude ser nulo ni estar vacio");
        }
    }
}
