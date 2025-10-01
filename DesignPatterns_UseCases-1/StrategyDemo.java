interface PaymentStrategy{void pay(int amt);}
class CreditCardPayment implements PaymentStrategy{
    public void pay(int amt){System.out.println("Paid " + amt + " by CreditCard");}
}
class UpiPayment implements PaymentStrategy{
    public void pay(int amt){System.out.println("Paid " + amt + " by UPI");}
}
class PaymentContext{
    private PaymentStrategy ps;
    public PaymentContext(PaymentStrategy ps){this.ps=ps;}
    public void execute(int amt){ps.pay(amt);}
}
public class StrategyDemo{
    public static void main(String[] args){
        PaymentContext ctx=new PaymentContext(new CreditCardPayment());
        ctx.execute(500);
        ctx=new PaymentContext(new UpiPayment());
        ctx.execute(200);
    }
}
