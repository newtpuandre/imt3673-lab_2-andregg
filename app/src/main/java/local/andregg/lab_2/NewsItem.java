package local.andregg.lab_2;


public class NewsItem {
    private String link;
    private String header;
    private String description;

    NewsItem(){}

    NewsItem(String m_link, String m_header, String m_description){
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

}
