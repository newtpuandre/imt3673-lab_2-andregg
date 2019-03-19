package local.andregg.lab_2;


/* Class for containing news information */
public class NewsItem {
    private int number;
    private String link;
    private String header;
    private String description;

    NewsItem(int m_number, String m_link, String m_header, String m_description){
        this.number = m_number;
        this.link = m_link;
        this.header = m_header;
        this.description = m_description;
    }

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
