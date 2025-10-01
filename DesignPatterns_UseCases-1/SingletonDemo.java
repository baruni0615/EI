class Database {
    private static Database instance;
    private Database(){}
    public static synchronized Database get(){
        if(instance==null)instance=new Database();
        return instance;
    }
    public void query(String q){System.out.println("Executing: "+q);}
}
public class SingletonDemo{
    public static void main(String[] args){
        Database db=Database.get();
        db.query("SELECT * FROM users");
    }
}
