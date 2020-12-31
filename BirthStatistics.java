package part1;

import csv.CSVParser;
import csv.CSVRecord;
import csv.SEFileUtil;

import java.io.File;
import java.util.List;

public class BirthStatistics {

    public final String pathToDirCSVs;

    public BirthStatistics (String pathCSVs){
        pathToDirCSVs = pathCSVs;
    }

    /**
     * This method returns the path to the CSV file of the specified year
     * @param year
     * @return
     */
    private String getPathToCSV (int year){
        File[] csvFiles = new File (pathToDirCSVs).listFiles();
        for (File csvF : csvFiles){
            if (csvF.getName().contains(Integer.toString(year))){
                return csvF.getAbsolutePath();
            }
        }
        return null;
    }

    /**
     * This method returns the row number in the CSV file of the
     * most popular name by the given gender
     * @param year
     * @param gender
     * @return
     */
    private int getCsvRowOfMostPopularNameByGender(int year, String gender){
        int rank = -1;
        SEFileUtil seFileUtil = new SEFileUtil(getPathToCSV(year));
        for (CSVRecord record : seFileUtil.getCSVParser()) {
            String currGender = record.get(1);
            if (currGender.equals(gender)){
                rank = (int) record.getRecordNumber();
                break;
            }
        }
        return rank;
    }

    private int getRank (int year, String name, String gender) {
        // This function returns the rank of a combination of
        // name and gender in a given year.
        SEFileUtil seFileUtil = new SEFileUtil(getPathToCSV(year));
        CSVParser parser = seFileUtil.getCSVParser(false, ",");
        List<CSVRecord> records = parser.getRecords();
        int row = 0;
        int rank = -1;
        for (CSVRecord record : records){
            row++;
            if (name.equals(record.get(0)) && gender.equals(record.get(1))){
                if (gender == "F") {
                    rank = row;
                }
                else rank = 1 + row - getCsvRowOfMostPopularNameByGender(year,
                        "M");
            }
        }
        return rank;
    }


    private int yearOfHighestRank (int firstYear, int lastYear,
                                   String name, String gender){
        // This function returns a year with the highest rank for
        // the name and gender given.
        int maxRank = getRank(firstYear, name, gender);
        int yearOfMaxRank = firstYear;
        int rank = 0;
        boolean flag = false;
        for (int year = firstYear; year <= lastYear; year++){
            rank = getRank(year, name, gender);
            if ((rank < maxRank && rank != -1) || maxRank == -1){
                maxRank = rank;
                yearOfMaxRank = year;
                flag = true;
            }
        }
        if (flag == false){return -1;}
        else return yearOfMaxRank;
    }


    private double getAverageRank (int firstYear,
                                   int lastYear, String name, String gender){
        // This function return the average of the ranks that a
        // combination of name and gender gets in a range of years.
        int counter = 0;
        double totalRank = 0, rank = 0;
        for (int year = firstYear; year <= lastYear; year++){
            rank = getRank(year, name, gender);
            if (rank != -1) {
                totalRank += rank;
                counter ++;
            }
        }
        if (totalRank == 0){return -1;}
        else return (totalRank/counter);
    }




    private String getName (int year, int rank, String gender){
        // This function returns the name of the child with
        // the given rank and gender
        int counter = 0;
        String t = "";
        SEFileUtil seFileUtil = new SEFileUtil(getPathToCSV(year));
        for (CSVRecord record : seFileUtil.getCSVParser()) {
            String currGender = record.get(1);
            if(currGender.equals(gender)){ counter ++;}
            if (counter == rank) {
                String name = record.get(0);
                return name;
            }
        }
        return "NO NAME";
    }


    private void totalBirths (int year) {
        // This function caculates the total births - seperating boys and girls
        SEFileUtil seFileUtil = new SEFileUtil(getPathToCSV(year));
        int numOfGirls = 0,numOfBoys = 0;
        for (CSVRecord record : seFileUtil.getCSVParser())
        {
            String currGender = record.get(1);
            if (currGender.equals("F")) {
                String sameNameGirlsNum = record.get(2);
                numOfGirls = numOfGirls + Integer.parseInt(sameNameGirlsNum);
            }
            else if (currGender.equals("M")){
                String sameNameBoysNum = record.get(2);
                numOfBoys = numOfBoys + Integer.parseInt(sameNameBoysNum);
            }
        }
        int numOfBirths = numOfBoys + numOfGirls;
        System.out.println("total births = " + numOfBirths);
        System.out.println("female girls = " + numOfGirls);
        System.out.println("male boys = " + numOfBoys);
    }


    private int getTotalBirthsRankedHigher(int year, String name, String gender){
        // This function returns the total births of children with
        // a higher rank than the given one
        SEFileUtil seFileUtil = new SEFileUtil(getPathToCSV(year));
        int rank = getRank(year, name, gender);
        int totalBirthsRankedHigher = 0, rankCounter = 0;
        for (CSVRecord record : seFileUtil.getCSVParser()){
            String currGender = record.get(1);
            if(currGender.equals(gender)){
                totalBirthsRankedHigher += Integer.parseInt(record.get(2));
                rankCounter += 1;
                if(rankCounter == (rank - 1)){break;}
            }
        }
        return totalBirthsRankedHigher;
    }


    public static void main(String[] args) {
        BirthStatistics birthStatistics = new BirthStatistics(args[0]);
        birthStatistics.totalBirths(2010);
        int rank = birthStatistics.getRank(2010, "Asher", "M");
        System.out.println("Rank is: " + rank);
        String name = birthStatistics.getName(2012, 10, "M");
        System.out.println("Name: " + name);
        System.out.println(birthStatistics.yearOfHighestRank(1880,
                2010,"David", "M"));
        System.out.println(birthStatistics.yearOfHighestRank(1880,
                2014,"Jennifer", "F"));
        System.out.println(birthStatistics.getAverageRank(1880,
                2014, "Benjamin", "M"));
        System.out.println(birthStatistics.getAverageRank(1880,
                2014, "Lois", "F"));
        System.out.println(birthStatistics.getTotalBirthsRankedHigher(2014,
                "Draco", "M"));
        System.out.print(birthStatistics.getTotalBirthsRankedHigher(2014,
                "Sophia", "F"));
        System.out.println();
    }


}