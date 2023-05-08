/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eggeducation.noticia.servicios;

import com.eggeducation.noticia.entidades.Imagen;
import com.eggeducation.noticia.entidades.Usuario;
import com.eggeducation.noticia.enumeraciones.Rol;
import com.eggeducation.noticia.excepciones.MiException;
import com.eggeducation.noticia.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author hdsot
 */
@Service
public class UsuarioServicio implements UserDetailsService {
    
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private ImagenServicio imagenServicio;
    
    @Transactional
    public void registrar(MultipartFile archivo,String nombre,String email, String password,String password2) throws MiException{
        validar(nombre,email,password,password2);
        
        Usuario usuario=new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        //usuario.setPassword(password);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setRol(Rol.USER);
        //cargamos la imagen
        Imagen imagen=imagenServicio.guardar(archivo);
        usuario.setImagen(imagen);
        
        usuarioRepositorio.save(usuario);
    }
    @Transactional
    public void actualizar(MultipartFile archivo, String idUsuario, String nombre, String email, String password, String password2) throws MiException {

        validar(nombre, email, password, password2);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setNombre(nombre);
            usuario.setEmail(email);

            usuario.setPassword(new BCryptPasswordEncoder().encode(password));

            usuario.setRol(Rol.USER);
            
            String idImagen = null;
            
            if (usuario.getImagen() != null) { //si hay imagen para cargar , la cargamos
                idImagen = usuario.getImagen().getId();
            }
            
            Imagen imagen = imagenServicio.actualizar(archivo, idImagen);
            
            usuario.setImagen(imagen);
            
            usuarioRepositorio.save(usuario);
        }

    }
    
    public Usuario getOne(String id){
        return usuarioRepositorio.getOne(id);
    }
    
    @Transactional//(readOnly=True)
    public List<Usuario> listarUsuarios() {

        List<Usuario> usuarios = new ArrayList();

        usuarios = usuarioRepositorio.findAll();

        return usuarios;
    }
    
    @Transactional
    public void cambiarRol(String id){
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
    	
    	if(respuesta.isPresent()) {
    		
    		Usuario usuario = respuesta.get();
    		
    		if(usuario.getRol().equals(Rol.USER)) {
    			
    		usuario.setRol(Rol.ADMIN);
    		
    		}else if(usuario.getRol().equals(Rol.ADMIN)) {
    			usuario.setRol(Rol.USER);
    		}
    	}
    }
    
    private void validar(String nombre,String email,String password,String password2) throws MiException{
        
        if(nombre==null||nombre.isEmpty()){
            throw new MiException("El nombre no pude ser nulo ni estar vacio");
        }
        if(email==null||email.isEmpty()){
            throw new MiException("El email no pude ser nulo ni estar vacio");
        }
        if(password==null||password.isEmpty()||password.length()<=5){
            throw new MiException("El password no pude ser nulo ni estar vacio y debe tener mas de 5 caracteres");
        }
        if(!password.equals(password2)){
            throw new MiException("Las contraseñas ingresadas deben ser iguales");
        }
    }        

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);

        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList();
            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());//ROLE_USER
            permisos.add(p);
            //de el repositorio y de la pagina 4 del manual, utilizamos los atributos que nos otorga el pedido al
            //servlet,para poder guardar informacion en nuestra httpSession
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

            HttpSession session = attr.getRequest().getSession(true);

            session.setAttribute("usuariosession", usuario);
            return new User(usuario.getEmail(), usuario.getPassword(), permisos);//retornamos un User con su email,contraseña
                                                                                    //y permiso
        }else{
        return null;
        }
    }
    
}
