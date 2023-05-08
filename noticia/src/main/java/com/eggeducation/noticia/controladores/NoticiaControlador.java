/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eggeducation.noticia.controladores;

import com.eggeducation.noticia.entidades.Noticia;
import com.eggeducation.noticia.excepciones.MiException;
import com.eggeducation.noticia.servicios.NoticiaServicio;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author hdsot
 */
@Controller
@RequestMapping("/noticia")
public class NoticiaControlador {
    
    @Autowired
    private NoticiaServicio noticiaServicio;
    
    @GetMapping("/")
    public String index(ModelMap modelo){
        List<Noticia> noticias = noticiaServicio.listarNoticia();
        modelo.addAttribute("noticias", noticias);
        return "index.html";
    }
    @GetMapping("/registrar")  //localhost:8080/registrar
    public String registrar(){
        return "noticia_form.html";
    }
    @PostMapping("/registro")
    public String registro(@RequestParam String titulo,@RequestParam String seccion,
            @RequestParam String cuerpo,ModelMap modelo){
        try {
            noticiaServicio.crearNoticia(titulo, seccion, cuerpo);
            modelo.put("exito","La noticia fue cargada");
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            Logger.getLogger(NoticiaControlador.class.getName()).log(Level.SEVERE, null, ex);
            return "noticia_form.html";
        }
        List<Noticia> noticias = noticiaServicio.listarNoticia();
        modelo.addAttribute("noticias", noticias);
        return "index.html";
    }
    @GetMapping("vista/{id}")
    public String vista(@PathVariable String id, ModelMap modelo){
        modelo.put("noticia", noticiaServicio.getOne(id));
        return "vista_noticia.html";
    }
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable String id, ModelMap modelo){
        modelo.put("noticia", noticiaServicio.getOne(id));
        return "modificar.html";
    }
    @GetMapping("/lista")
    public String listar(ModelMap modelo){
        List<Noticia> noticias= noticiaServicio.listarNoticia();
        modelo.addAttribute("noticias", noticias);
        return "noticia_list.html";
    }
    @PostMapping("modificarNoticia/{id}")
    public String modificarNoticia(@PathVariable String id,String titulo,String cuerpo,String seleccion,ModelMap modelo){
        try{
            noticiaServicio.modificarNoticia(id, titulo, seleccion, cuerpo);
            return "redirect:../lista";
        } catch (MiException ex){
            modelo.put("error",ex.getMessage());
            return "modificar.html";
        }
    }
    @GetMapping("/noticias_egg")
    public String listaNoticias(){
        return "noticia_list.html";
    }
}