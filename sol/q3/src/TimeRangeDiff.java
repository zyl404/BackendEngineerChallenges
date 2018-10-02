import com.sun.tools.javac.util.Pair;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

public class TimeRangeDiff {
    private static Pair<LocalTime, LocalTime> getBoundary(String timeString) {
        String[] boundary = timeString.split("-");
        return new Pair<>(LocalTime.parse(boundary[0]), LocalTime.parse(boundary[1]));
    }
    
    private static String timeToStr(LocalTime low, LocalTime high) {
        return low.toString() + "-" + high.toString();
    }

    private static List<Pair<LocalTime, LocalTime>> getDiff(Pair<LocalTime, LocalTime> minuend,
                                                           Pair<LocalTime, LocalTime> subtrahend) {
        LocalTime minuend_low = minuend.fst;
        LocalTime minuend_high = minuend.snd;
        LocalTime subtrahend_low = subtrahend.fst;
        LocalTime subtrahend_high = subtrahend.snd;

        // subtrahend_high <= minuend_low
        // return [(minuend_low, minuend_high)]
        if (subtrahend_high.compareTo(minuend_low) <= 0){
            return new ArrayList<Pair<LocalTime, LocalTime>>() {{
                add(new Pair<>(minuend_low, minuend_high));
            }};
        }

        // subtrahend_low >= minuend_high
        // return [(minuend_low, minuend_high)]
        if (subtrahend_low.compareTo(minuend_high) >= 0){
            return new ArrayList<Pair<LocalTime,LocalTime>>() {{
                add(new Pair<>(minuend_low, minuend_high));
            }};
        }

        // minuend_low < subtrahend < minuend_high && minuend_high <= subtrahend_high
        // return [(minuend_low, subtrahend_low)]
        if (minuend_low.compareTo(subtrahend_low)< 0
                && subtrahend_low.compareTo(minuend_high) < 0 &&
                minuend_high.compareTo(subtrahend_high) <= 0) {
            return new ArrayList<Pair<LocalTime, LocalTime>>(){{
                add(new Pair<>(minuend_low, subtrahend_low));
            }};
        }

        // minuend_low < subtrahend_low && subtrahend_high < minuend_high
        //return [(minuend_low, subtrahend_low), (subtrahend_high, minuend_high)]
        if (minuend_low.compareTo(subtrahend_low) < 0 && subtrahend_high.compareTo(minuend_high) < 0){
            return new ArrayList<Pair<LocalTime, LocalTime>>(){{
                add(new Pair<>(minuend_low, subtrahend_low));
                add(new Pair<>(subtrahend_high, minuend_high));
            }};
        }

        // subtrahend_low <= minuend_low && minuend_low < subtrahend_high < minuend_high
        // return [(subtrahend_high, minuend_high)]
        if (subtrahend_low.compareTo(minuend_low) <= 0 &&
                minuend_low.compareTo(subtrahend_high) < 0 &&
               subtrahend_high.compareTo(minuend_high) < 0){
            return new ArrayList<Pair<LocalTime, LocalTime>>() {{
                add(new Pair<>(subtrahend_high, minuend_high));
            }};
        }

        // subtrahend_low <= minuend_low && minuend_high <= subtrahend_high
        // return []
        if (subtrahend_low.compareTo(minuend_low) <= 0 && minuend_high.compareTo(subtrahend_high) <= 0) {
            return new ArrayList<Pair<LocalTime, LocalTime>>();
        }
        
        return new ArrayList<Pair<LocalTime, LocalTime>>();
    }

    private static List<Pair<LocalTime, LocalTime>> batch_compare(List<Pair<LocalTime, LocalTime>> fromTimelist,
                                                                     List<Pair<LocalTime, LocalTime>> excludTimeList) {
        List<Pair<LocalTime, LocalTime>> result = fromTimelist;
        for (Pair<LocalTime, LocalTime> i : excludTimeList) {
            List<Pair<LocalTime, LocalTime>> buffer = new ArrayList<>(result);
            result.clear();
            while (buffer.size()>0) {
                Pair<LocalTime, LocalTime> item  = buffer.remove(0);
                List<Pair<LocalTime, LocalTime>> temp = getDiff(item, i);
                if (temp.size() > 0) {
                    result.addAll(temp);
                }
            }
        }

        return result;
    }


    private static List<Pair<LocalTime, LocalTime>> fromStrToTimeList(String s) {
        String[] buffer = s.replace("(", "").replace(")", "").split(", ");
        List<Pair<LocalTime, LocalTime>> timeList = new ArrayList<>();
        for (String i : buffer) {
            timeList.add(getBoundary(i));
        }
        return timeList;
    }

    public static void main(String[] args) {
        // The program will calculate aTimeStr - bTimeStr.
        // Make sure the time format is hh:mm, so if the time is 9:00, please change it to 09:00.
        
        String aTimeStr = "(09:00-11:00, 13:00-15:00)";
        String bTimeStr = "(09:00-09:15, 10:00-10:15, 12:30-16:00)";
        // String aTimeStr = "(09:00-10:00)";
        // String bTimeStr = "(09:00-09:30)";
        List<Pair<LocalTime, LocalTime>> aTimeList = fromStrToTimeList(aTimeStr);
        List<Pair<LocalTime, LocalTime>> bTimeList = fromStrToTimeList(bTimeStr);
        List<Pair<LocalTime, LocalTime>> result = batch_compare(aTimeList, bTimeList);
        List<String> output = new ArrayList<>();
        for (Pair<LocalTime, LocalTime> i : result) {
            output.add(timeToStr(i.fst, i.snd));
        }
        System.out.println(output.toString().replace("[", "(").replace("]", ")"));
    }

}