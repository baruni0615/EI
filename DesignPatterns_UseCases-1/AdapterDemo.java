interface MediaPlayer{void play(String file);}
class Mp3Player implements MediaPlayer{
    public void play(String file){System.out.println("Playing mp3: "+file);}
}
class Mp4Library{public void playMp4(String file){System.out.println("Playing mp4: "+file);}}
class Mp4Adapter implements MediaPlayer{
    private Mp4Library lib=new Mp4Library();
    public void play(String file){lib.playMp4(file);}
}
public class AdapterDemo{
    public static void main(String[] args){
        MediaPlayer mp3=new Mp3Player();
        MediaPlayer mp4=new Mp4Adapter();
        mp3.play("song.mp3");
        mp4.play("video.mp4");
    }
}
