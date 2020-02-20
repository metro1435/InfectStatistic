import java.io.*;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InfectStatistic
{

    //命令对象类
    static class Command
    {
        private static boolean list;
        public static boolean islist(){return list;}
        public static void setlist(boolean li){list=li;}
    }

    //正则表达式类
    //存储正确格式的正则表达式
    //匹配输入字符串
    //根据匹配结果返回Province_stats对象
    static class RegexPtn
    {
        public static String IpIncrease="(\\S+)新增 感染患者(\\d+)人";
        public static String SpIncrease="(\\S+) 新增 疑似患者(\\d+)人";
        public static String IpFlow="(\\S+) 感染患者 流入 (\\S+) (\\d+)人";
        public static String SpFlow="(\\S+) 疑似患者 流入(\\S+) (\\d+)人";
        public static String DeadIncrease="(\\S+) 死亡 (\\d+)人";
        public static String CureIncrease="(\\S+) 治愈 (\\d+)人";
        public static String SpInfected="(\\S+) 疑似患者 确认感染(\\d+)人";
        public static String SpExcluded="(\\S+) 排除 疑似患者 (\\d+)人";

        public static ArrayList<Province_stats> anaLine(String line)
        {
            ArrayList<Province_stats> res =new ArrayList<Province_stats>();
            if(line.matches(IpIncrease))
            {

                res.add(addIp(line));
            }
            else if(line.matches(SpIncrease))
            {
                res.add(addSp(line));
            }
            else if(line.matches(IpFlow)) return Ipflow(line);
            else if(line.matches(SpFlow)) return Spflow(line);
            else if(line.matches(DeadIncrease))
            {
                res.add(addDead(line));
            }
            else if(line.matches(CureIncrease))
            {
                res.add(addCure(line));
            }
            else if(line.matches(SpInfected))
            {
                res.add(spInfected(line));
            }
            else if(line.matches(SpExcluded))
            {
                res.add(spExcluded(line));
            }
            else
            {
                System.out.println(line+"格式不正确");
                return null;
            }
            return res;
        }
        public static Province_stats addIp(String line)//IpIncrease
        {
            Pattern reg=Pattern.compile(IpIncrease);
            Matcher m=reg.matcher(line);
            if(m.find())
            {

                Province_stats provs=new Province_stats(m.group(1),Integer.parseInt(m.group(2)),0,0,0);
                return provs;
            }
            else return null;
        }
        public static Province_stats addSp(String line)
        {
            Pattern reg=Pattern.compile(SpIncrease);
            Matcher m=reg.matcher(line);
            if(m.find())
            {
                Province_stats provs=new Province_stats(m.group(1),0,Integer.parseInt(m.group(2)),0,0);
                return provs;
            }
            else return null;
        }

        public static ArrayList<Province_stats> Ipflow(String line)//IpFlow
        {
            Pattern reg=Pattern.compile(IpFlow);
            Matcher m=reg.matcher(line);
            ArrayList<Province_stats> res=new ArrayList<Province_stats>();
            if(m.find())
            {
                Province_stats prov1=new Province_stats(m.group(1),-Integer.parseInt(m.group(3)),0,0,0);
                Province_stats prov2=new Province_stats(m.group(2),Integer.parseInt(m.group(3)),0,0,0);
                res.add(prov1);
                res.add(prov2);
                return res;
            }
            else return null;
        }

        public static ArrayList<Province_stats> Spflow(String line)//SpFlow
        {
            Pattern reg=Pattern.compile(SpFlow);
            Matcher m=reg.matcher(line);
            ArrayList<Province_stats> res=new ArrayList<Province_stats>();
            if(m.find())
            {
                Province_stats prov1=new Province_stats(m.group(1),0,-Integer.parseInt(m.group(3)),0,0);
                Province_stats prov2=new Province_stats(m.group(2),0,Integer.parseInt(m.group(3)),0,0);
                res.add(prov1);
                res.add(prov2);
                return res;
            }
            else return null;
        }
        public static Province_stats addDead(String line)//DeadIncrease
        {
            Pattern reg=Pattern.compile(DeadIncrease);
            Matcher m=reg.matcher(line);
            if(m.find())
            {

                Province_stats provs=new Province_stats(m.group(1),0,0,0,Integer.parseInt(m.group(2)));
                return provs;
            }
            else return null;
        }
        public static Province_stats addCure(String line)//CureIncrease
        {
            Pattern reg=Pattern.compile(CureIncrease);
            Matcher m=reg.matcher(line);
            if(m.find())
            {

                Province_stats provs=new Province_stats(m.group(1),0,0,Integer.parseInt(m.group(2)),0);
                return provs;
            }
            else return null;
        }
        public static Province_stats spInfected(String line)//SpInfected
        {
            Pattern reg=Pattern.compile(SpInfected);
            Matcher m=reg.matcher(line);
            if(m.find())
            {

                Province_stats provs=new Province_stats(m.group(1),Integer.parseInt(m.group(2)),-Integer.parseInt(m.group(2)),0,0);
                return provs;
            }
            else return null;
        }
        public static Province_stats spExcluded(String line)//if匹配SpExcluded
        {
            Pattern reg=Pattern.compile(SpExcluded);
            Matcher m=reg.matcher(line);
            if(m.find())
            {

                Province_stats provs=new Province_stats(m.group(1),0,-Integer.parseInt(m.group(2)),0,0);
                return provs;
            }
            else return null;
        }
    }

    //入口主函数，接收命令行参数
    public static void main(String[] args)
    {

        if(args[0].equals("list"))
        {
            Command.setlist(true);
            System.out.println("yes!");
            ListArgs listArgs=new ListArgs();
            for(int i=1;i<args.length-1;i++)
            {
                if(args[i].equals("-log"))
                {
                    listArgs.set_log(args[i + 1]);
                }
                else if(args[i].equals("-out"))
                {
                    listArgs.set_out(args[i+1]);
                }
                else if(args[i].equals("-date"))
                {
                    listArgs.set_date(args[i+1]);
                }
            }
            Log thislog=new Log();
            thislog.setLogDir(listArgs.get_log_content());
            for(File logfile:thislog.getLogfiles())//读取log目录下文件，对每个文件
            {
                LogController logctr=new LogController(logfile);//建立文件控制类
                DailyResult dailyResult=new DailyResult();//存储解析结果
                ArrayList<String> list=logctr.readLine();//读取文件的一行
                ArrayList<Province_stats> logresultList=new ArrayList<Province_stats>();
                for(String stat:list)
                {
                    for(Province_stats logres:RegexPtn.anaLine(stat))//逐行解析
                    logresultList.add(logres);//结果存储
                    dailyResult.acceptLogResult(logresultList);
                    dailyResult.calculateWhole();
                    try {
                        BufferedWriter out = new BufferedWriter(new FileWriter(listArgs.get_out_content()));
                        out.write(dailyResult.getWhole().outputLine());
                        for(Province_stats item:dailyResult.getResults())
                        {
                            out.write(item.outputLine());
                        }
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        else System.out.println("未传入必要指令list");

    }
}

//结果处理类
//合并、统计日志解析后的结果
class DailyResult
{
    public static final String[] PROVINCES={"安徽","北京","重庆","福建","甘肃",
            "广东","广西","贵州","海南","河北",
            "河南","黑龙江","湖北","湖南","江西",
            "吉林","江苏","辽宁","内蒙古","宁夏",
            "青海","山东","山西","陕西","上海",
            "四川","天津","西藏","新疆","云南","浙江"};
    public static final int PROVINCE_NUM=31;
    private Province_stats[] resultlist;
    private Province_stats whole;
    public Province_stats getWhole()
    {
        return whole;
    }
    public DailyResult()
    {
        whole.setProv("全国");
        resultlist=new Province_stats[PROVINCE_NUM];
        for(int i=0;i<PROVINCE_NUM;i++)
        {
            resultlist[i]=new Province_stats(PROVINCES[i]);
        }
    }
    public void acceptLogResult(ArrayList<Province_stats> logres)
    {
        for(Province_stats res:logres)
        {
            for (Province_stats item : resultlist)
            {
                if (res.Province().equals(item.Province()))
                {
                    item.addBy(res);
                }
            }
        }
    }
    public void calculateWhole()
    {
        for(Province_stats item:resultlist)
        {
            whole.setInfectP(whole.InfectP()+item.InfectP());
            whole.setSuspectP(whole.SuspectP()+item.SuspectP());
            whole.setCures(whole.Cures()+item.Cures());
            whole.setDeads(whole.Deads()+item.Deads());
        }
    }
    public ArrayList<Province_stats> getResults()
    {
        ArrayList<Province_stats> res=new ArrayList<Province_stats>();
        for(Province_stats provst:resultlist)
        {
            if(provst.InfectP()==0 || provst.SuspectP()==0 || provst.Cures()==0 || provst.Deads()==0){}
            else
            {
                res.add(provst);
            }
        }
        return res;
    }
}

//省份信息结构类
class Province_stats
{
    private String prov;
    private int infected_patients;
    private int suspect_patients;
    private int cures;
    private int deads;
    public Province_stats(){}
    public Province_stats(String prov_name,int ip,int sp,int cu,int dd)
    {

            prov=prov_name;
            infected_patients=ip;
            suspect_patients=sp;
            cures=cu;
            deads=dd;

    }
    public Province_stats(String prov_name)
    {
        prov=prov_name;
    }
    public void setInfectP(int ip) { infected_patients=ip;}
    public void setSuspectP(int sp){suspect_patients=sp;}
    public void setCures(int cu){cures=cu;}
    public void setDeads(int dd){deads=dd;}
    public void setProv(String prov_name){prov=prov_name;}
    public String Province(){return this.prov;}
    public int InfectP(){return this.infected_patients;}
    public int SuspectP(){return this.suspect_patients;}
    public int Cures(){return this.cures;}
    public int Deads(){return this.deads;}
    public void addInfectP(int num){this.infected_patients+=num;}
    public void addSuspectP(int num){this.suspect_patients+=num;}
    public void addBy(Province_stats item)
    {
        this.infected_patients+=item.InfectP();
        this.suspect_patients+=item.SuspectP();
        this.deads+=item.Deads();
        this.cures+=item.Cures();
    }
    public String outputLine()
    {
        return this.Province()+" 感染患者："+this.InfectP()+" 疑似患者："+this.SuspectP()+" 治愈："+this.Cures()+" 死亡："+this.Deads()+"\n";
    }
}

//命令行参数结构类
class ListArgs
{
    private String log_content;
    private String out_content;
    private String date_content;
    public String get_log_content(){return log_content;}
    public String get_out_content(){return out_content;}
    public String get_date_content(){return date_content;}
    public void set_log(String lc){log_content=lc;}
    public void set_out(String oc){out_content=oc;}
    public void set_date(String dc){date_content=dc;}
    public ListArgs(){}
}

//日志文件结构类
class Log
{
    private File LogDir;
    private File[] logfiles;

    public Log(){}

    public File getLogDir() { return LogDir;}
    public File[] getLogfiles(){return logfiles;}
    public void setLogDir(String logPath)
    {
        File logDir=new File(logPath);
        if(logDir.exists())
        {
            LogDir = logDir;
            logfiles=logDir.listFiles();
        }
        else System.out.println("Input path not exist!");
    }

}

//日志文件控制类
//验证并读取日志内容
class LogController
{
    private File log;
    public LogController(File mlog)
    {
        log=mlog;
    }
    public ArrayList<String> readLine()
    {
       ArrayList<String> readList=new ArrayList<String>();
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(log));
            String str;
            while ((str = in.readLine()) != null)
            {
                readList.add(str);
            }
            return readList;
        }
        catch (IOException e) {}
        return readList;
    }
}




