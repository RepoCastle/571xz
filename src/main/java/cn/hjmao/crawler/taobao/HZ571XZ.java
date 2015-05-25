package cn.hjmao.crawler.taobao;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hjmao on 5/25/15.
 */
public class HZ571XZ {
  private static final String baseUrl = "http://hz.571xz.com";
  private static final String baseShopUrl = baseUrl + "/shop.htm";
  private static final String huoyuanTag = "huoyuan";
  private static final List<String> huoyuanList = Arrays.asList("dianshangjidi", "nanzhuangjingpin", "zhidiguoji", "sijixingzuo");

  private Document getFromUrl(String url) {
    Document doc = null;
    try {
      doc = Jsoup.connect(url).userAgent("Mozilla").get();
    } catch (IOException ioe) {
      doc = null;
    }
    return doc;
  }
  public List<Dangkou> getDangkous(String huoyuan) {
    List<Dangkou> dangkous = new ArrayList<Dangkou>();

    String huoyuanUrl = baseUrl + "/" + huoyuanTag + "/" + huoyuan;
    Document doc = getFromUrl(huoyuanUrl);
    if (doc != null) {
      Elements shops = doc.select("a[href~=" + baseShopUrl + "]");
      for (Element shop: shops) {
        String dangkouNum = shop.text();
        String dangkouUrl = shop.attr("href");
        Dangkou dangkou = new Dangkou(dangkouNum, dangkouUrl);
        dangkous.add(dangkou);
      }
    } else {
      System.err.println("Can not get content from huoyuan " + huoyuan + "@" + huoyuanUrl);
    }

    return dangkous;
  }

  public class Dangkou {
    private String num;
    private String url;

    public Dangkou(String num, String url) {
      this.num = num.trim();
      this.url = url.trim();
    }

    public List<Good> getGoods() {
      List<Good> goods = new ArrayList<Good>();

      for (int i=1; ; i++) {
        String thisUrl = this.getUrl() + "&pageNo=" + i + "&timeflag=2";
        Document doc = getFromUrl(thisUrl);
        if (doc != null) {
          Elements gdListSelector = doc.select("ul.gdlist");
          Elements gdList = gdListSelector.first().select("li");
          if (gdList.size() == 0) {
            break;
          }
          for (Element goodLi: gdList) {
            String title = goodLi.select(".title").text();
            String price = goodLi.select("span").first().text();
            Element aNode = goodLi.select(".img").first().select("a").first();
            String url = baseUrl + "/" + aNode.attr("href");
            String avatar = aNode.select("img").first().attr("src");
            Good good = new Good(title, price, url, avatar);
            goods.add(good);
          }
        } else {
          System.err.println("Can not get content from Dangkou " + this.getNum() + "@" + this.getUrl());
        }
      }

      return goods;
    }

    public String getNum() {
      return this.num;
    }
    public String getUrl() {
      return this.url;
    }
  }
  public class Good {
    private String title;
    private String price;
    private String url;
    private String avatar;

    public Good(String title, String price, String url, String avatar) {
      this.title = title;
      this.price = price;
      this.url = url;
      this.avatar = avatar;
    }

    public String getTitle() {
      return title;
    }

    public String getPrice() {
      return price;
    }

    public String getUrl() {
      return url;
    }

    public String getAvatar() {
      return avatar;
    }
  }

  public static void main(String[] args) {
    HZ571XZ hz571xz = new HZ571XZ();

    for (String huoyuan: huoyuanList) {
      List<Dangkou> dangkous = hz571xz.getDangkous(huoyuan);
      for (Dangkou dangkou: dangkous) {
        List<Good> goods = dangkou.getGoods();
        for (Good good: goods) {

        }
        break;
      }
      System.out.println(dangkous.size());
      break;
    }
  }
}
