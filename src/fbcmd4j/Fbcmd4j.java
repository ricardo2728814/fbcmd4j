package fbcmd4j;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonObject;
import com.restfb.json.JsonValue;
import com.restfb.types.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Fbcmd4j {
    private static FacebookClient fb;
    private static String user_access_token = "EAAQcZB4Y4J4gBAEaD4BHA0tGD8xjaftMTcgr0D6gfbUimDZC8jLuG0W9ZC3voZBmnXshlJcZAAJ7HPYkmvempvYRZBt9AKtdaUTaKmqUTnRCnM8JTjv8WlhxXhvSlfsEV45ZA2zwLSsee2MVM16NjNfZAUZBSErXXPcZA4Fe7gikDZCZBU5eD4JKNd9YngsDZC7hTZAkOT5EguznrA1OxCotb4JI965BVwsBZAC9L8ZD";
    private static Scanner sn = new Scanner(System.in);
    private static final Logger logger = LogManager.getLogger();
    
    public static void main(String[] args)  {
        fb = new DefaultFacebookClient(user_access_token, Version.VERSION_3_2);
        
        while(true){
            System.out.println("Porfavor inserte un comando:");
            System.out.println("\tfeed");
            System.out.println("\tusuario");
            System.out.println("\tsalir");
            System.out.println("Comando:\t");
            String cmd = sn.nextLine();
            String data;
            System.out.println("");
            logger.info("Comando: {}", cmd);
            switch(cmd){
                case "salir":
                    return;
                case "feed":
                    System.out.println("¿Cantidad de posts a conseguir?");
                    int qt = 0;
                    do{
                        try{
                            qt = Integer.parseInt( sn.nextLine() );
                        } catch(Exception e){
                            qt = 0;
                            System.out.println("Ingrese un número igual o mayor a 1.");
                        }
                    }while(qt <= 0);
                    System.out.println("Feed del usuario:\n");
                    data = getFeed(qt);
                    break;
                case "usuario":
                    data = getUserData();
                    break;
                default:
                    System.out.println("Porfavor seleccione un comando valido");
                    logger.error("Comando : {} no es valido",cmd);
                    continue;
            }
            System.out.println(data);
            System.out.println("¿Desea almacenar los datos en un archivo? (si/no)");
            cmd = sn.nextLine();
            if(cmd.toUpperCase().equals("SI")){
                System.out.print("Nombre del archivo: ");
                cmd = sn.nextLine();
                try{
                    Path p = Files.write( Paths.get(cmd) , data.getBytes());
                    System.out.println("Archivo creado.\n");
                    logger.info("Archivo creado: ",p.toString());
                } catch (Exception e){
                    System.out.println("No se pudo escribir el archivo.\n");
                    logger.error("No se pudo crear el archivo: {}", cmd);
                }
                
                
            }
        }
        
    }
    public static String getFeed(int N){
        JsonObject feed = fb.fetchObject("me/feed", JsonObject.class);
        String data = "";
        int i = 0;
        for(JsonValue a : feed.get("data").asArray() ){
            if(i >= N) return data;
            JsonObject o = a.asObject();
            String msg = o.getString("message", "");
            String date = o.getString("created_time", "");
            String id = o.getString("id", "");
            String name = "";
            if(id != ""){
                String user_id = id.split("_")[0];
                JsonObject u = fb.fetchObject(user_id, JsonObject.class);
                name = u.getString("name", "");
            }
            data+="POST_ID\t"+id+"\n"
                    + "MESSAGE\t"+msg+"\n"
                    + "TIME\t"+date+"\n"
                    + "POST_BY\t"+name+"\n"
                    + "\n";
            logger.info("FEED: {}",o.toString());
            i++;
        }
        return data;
    }
    public static String getUserData(){
        String data = "";
        User me = fb.fetchObject("me", User.class, Parameter.with("fields", "email,birthday,gender,link,name"));
        data += "ID\t"+me.getId()+"\n"
                + "NAME\t"+me.getName()+"\n"
                + "GENDER\t"+me.getGender()+"\n"
                + "EMAIL\t"+me.getEmail()+"\n"
                + "BIRTHDAY\t"+me.getBirthday()+"\n"
                + "URL\t"+me.getLink()+"\n"
                + "\n";
        logger.info("USUARIO: {}", me.toString());
        return data;
    }
}