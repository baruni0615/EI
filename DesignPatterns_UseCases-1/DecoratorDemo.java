interface Coffee{String getDesc();int cost();}
class SimpleCoffee implements Coffee{
    public String getDesc(){return "Coffee";}
    public int cost(){return 10;}
}
class MilkDecorator implements Coffee{
    private Coffee base;public MilkDecorator(Coffee b){base=b;}
    public String getDesc(){return base.getDesc()+"+Milk";}
    public int cost(){return base.cost()+5;}
}
class SugarDecorator implements Coffee{
    private Coffee base;public SugarDecorator(Coffee b){base=b;}
    public String getDesc(){return base.getDesc()+"+Sugar";}
    public int cost(){return base.cost()+2;}
}
public class DecoratorDemo{
    public static void main(String[] args){
        Coffee c=new SimpleCoffee();
        c=new MilkDecorator(c);
        c=new SugarDecorator(c);
        System.out.println(c.getDesc()+" costs "+c.cost());
    }
}
