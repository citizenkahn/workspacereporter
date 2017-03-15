/**
 * Created by pkahn on 3/15/17.
 */

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter
import groovy.json.JsonSlurper

import javax.jws.WebResult;

class Walker {
  Client client

  public Walker() {
    client = Client.create()
    def base = "http://localhost:8080/api/linux"
    def data = getPathInfo(client, base)

    data['files'].each { fileMap ->
      println "${fileMap.name} ${fileMap.type}"
      if (fileMap.type == "directory") {
        def children = getPathInfo(client, "${base}/${fileMap.name}")
        children.each { println "\t${it}" }
      }

    }

  }

  private def getPathInfo(Client client, String base) {
    WebResource webResource = client.resource(base)

    def response = webResource.type("application/json").get(ClientResponse.class)

    def parser = new JsonSlurper()
    def data = parser.parseText(response.getEntity(String.class))
    webResource.delete()
    data
  }

  public static void main(String [] args) {
    Walker walker = new Walker()
  }
}
