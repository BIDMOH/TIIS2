package mobile.tiis.staging.mObjects;

/**
 * Created by issymac on 11/12/15.
 */
public class DrawerObjects {

    private String name;
    private int imageId;

    public DrawerObjects(String mName, int imageId){
        this.name    = mName;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
