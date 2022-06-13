package com.example.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Presencia;
import com.example.demo.dao.PresenciasDAO;

@RestController 

public class ControllerRest {

	@Autowired 
 	private PresenciasDAO presenciasDAO;
	
	@PostMapping
	public ResponseEntity<Presencia> crearPresencia(String name, double latitud, double longitud){
	    double latAustria = 41.4161732;
	    double longAustria = 2.1991057;
	    String comentario = "";
	    boolean esta_dentro = distancia2(latAustria, longAustria, latitud, longitud) < 25;
	    		
	    		//Math.acos(Math.sin(latAustria) * Math.sin(latitud) 
	    		//+ Math.cos(latAustria) * Math.cos(latitud) * Math.cos(longitud - (longAustria))) * 6371 <= 200; 
	    if(esta_dentro) {
	    	comentario = "Esta dentro del radio. He fichado";
	    } else {
	    	comentario = "Esta fuera del radio. No ha fichado";
	    }
	    
	    String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	    
	    Presencia presencia = new Presencia(name, latitud, longitud, comentario, fecha);
	    Presencia newPre = presenciasDAO.save(presencia);
	    return ResponseEntity.ok(newPre);
	}
	
	@GetMapping("")
	public ResponseEntity<List<Presencia>> getPresencias(){
		List<Presencia> presencias = presenciasDAO.findAll();
		return ResponseEntity.ok(presencias);
		
	}
	@GetMapping(value="{name}")
	public ResponseEntity<List<Presencia>> getPresenciasBy(@PathVariable("name") String name){
		List<Presencia> presencias = presenciasDAO.findByName(name);
		return ResponseEntity.ok(presencias);
	}
	public static double distancia1(double lat1, double long1, double lat2, double long2) {
	    return 6371*Math.acos(Math.sin(lat1) * Math.sin(lat2) +
	            Math.cos(lat1) * Math.cos(lat2) * Math.cos(long2 - (long1)));

	}
	public static double distancia2(double lat1, double lng1, double lat2, double lng2) {
	    //double radioTierra = 3958.75;//en millas
	    double radioTierra = 6371;//en kilómetros
	    double dLat = Math.toRadians(lat2 - lat1);
	    double dLng = Math.toRadians(lng2 - lng1);
	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);
	    double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
	    double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
	    double distancia = radioTierra * va2;

	    return distancia*1000;
	}

	private static double rad(double d) {
	    return d * Math.PI / 180.0;
	}

	public static double distancia3(double lat1,double lon1,double lat2, double lon2) {
	    double radLat1 = rad(lat1);
	    double radLat2 = rad(lat2);
	    double a = radLat1 - radLat2;
	    double b = rad(lon1) - rad(lon2);
	    double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
	    s = s * 6378137;
	    //s = Math.round(s * 10000) / 10000;
	    return s;
	}
}
