package local.andregg.lab_2;

/* Class for containing news information */
public class NewsItem {

    //Variables needed for the representation of the news items
    private int number;
    private String link = "null";
    private String header = "null";
    private String description = "null";

    //Constructor
    NewsItem(int m_number, String m_link, String m_header, String m_description){
        this.number = m_number;
        this.link = m_link;
        this.header = m_header;
        this.description = m_description;
    }

    //Getters
    public String returnLink(){
        return this.link;
    }

    public String returnHeader(){
        return this.header;
    }

    public String returnDescription(){
        return this.description;
    }

    public int returnNumber(){return this.number;}

}
