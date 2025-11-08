package entidades;

import java.util.ArrayList;
import java.util.List;

import utilidades.items.Item;

public class Mochila {
    private final List<Item> items = new ArrayList<>();

    public void add(Item item){ if(item!=null) items.add(item); }

    public boolean removeFirst(String itemId){
        for (int i=0;i<items.size();i++){
            if (items.get(i).getId().equals(itemId)) { items.remove(i); return true; }
        }
        return false;
    }

    public List<Item> getItems(){ return items; }
}
