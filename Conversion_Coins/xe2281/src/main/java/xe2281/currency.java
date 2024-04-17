package xe2281;
public class currency {
   private String id;
   private String type;
   private int amount;
   public currency(String i,String t,int a)
   {
	   this.id=i;
	   this.type=t;
	   this.amount=a;
   }
   public String getId() {return this.id;}
   public String getType() {return this.type;}
   public int getAmount() {return this.amount;}
}
