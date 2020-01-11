package com.bookshop.library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.expression.Lists;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
public class LibraryController {
    private String oldIsbn="";
    @Autowired
    bookRepo repo;
    @RequestMapping("/")
    public ModelAndView home() throws SQLException, ClassNotFoundException {
        refreshData();
        return drawHomePage();
    }
    @RequestMapping("addbook")
    public String addbook(){
        //repo.save(a);
        return "addbook.html";
    }

    @RequestMapping("editbook")
    public ModelAndView editbook(@RequestParam String isbn) throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:navin", "sa", "");
        ModelAndView mv = new ModelAndView("editbook.html");
        System.out.println("edit book with ISBN "+isbn);
        ArrayList<comment> arr = new ArrayList<>();
        oldIsbn=isbn;
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM COMMENT WHERE ISBN='"+isbn+"'");
        while (rs.next()) {
            comment b = new comment();
            b.setIsbn(rs.getString(4));

            String d=rs.getString(3);
            String date = d.substring(0,4)+"."+d.substring(4,6)+"."+d.substring(6,8);
            b.setDate(date);
            b.setId(rs.getInt(1));
            b.setComment(rs.getString(2));
            arr.add(b);
        }
        mv.addObject("num",isbn);
        mv.addObject("m",arr);
        conn.close();
        return mv;
    }
    
    @RequestMapping("add")
    public ModelAndView add(@RequestParam String isbn, @RequestParam String author,@RequestParam String title) throws ClassNotFoundException, SQLException {


        if(isbn!=""&&author!=""&&title!=""&&!ifEntryExists(isbn)) {

            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:mem:navin", "sa", "");

            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery("SELECT COUNT (COMMENT) FROM COMMENT WHERE ISBN='" + isbn + "'");
            int c = 0;
            while (rs.next()) {
                c = rs.getInt(1);
            }

            PreparedStatement p = conn.prepareStatement("INSERT INTO BOOK VALUES (?,?,?,?)");
            p.setString(1, isbn);
            p.setString(2, author);
            p.setInt(3, c);
            p.setString(4, title);
            p.execute();
            System.out.println("added " + isbn);
            conn.close();
        }
        return drawHomePage();
    }

    @RequestMapping("deletebook")
    public ModelAndView deletebook(@RequestParam String isbn) throws SQLException, ClassNotFoundException {
        ModelAndView mv;
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:navin","sa","");
        Statement st=conn.createStatement();

        PreparedStatement p = conn.prepareStatement("DELETE FROM BOOK WHERE ISBN=?");
        p.setString(1, isbn);
        p.execute();

        System.out.println("deleted "  + isbn);
        conn.close();
        return drawHomePage();
    }

    @RequestMapping("edit")
    public ModelAndView edit(@RequestParam String oldISBN, @RequestParam String ISBN,@RequestParam String newauthor,@RequestParam String newtitle) throws SQLException, ClassNotFoundException {

        Class.forName("org.h2.Driver");
        ArrayList<book> arr=new ArrayList<>();
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:navin","sa","");
        Statement st=conn.createStatement();
        //ResultSet rs = st.executeQuery("SELECT * FROM BOOK WHERE ISBN='"+ISBN+"'");
        if(ifEntryExists(oldISBN)) {
            oldIsbn = oldISBN;
            System.out.println(oldIsbn + "old isbn in EDIT");
            System.out.println("old iSBN " + oldISBN + " new " + ISBN);
            ResultSet rs = st.executeQuery("SELECT COUNT (COMMENT) FROM COMMENT WHERE ISBN='" + oldISBN + "'");
            int c = 0;
            while (rs.next()) {
                c = rs.getInt(1);
            }
            
            if (!ISBN.equals("")) {
                PreparedStatement p = conn.prepareStatement("UPDATE COMMENT SET ISBN=? WHERE ISBN=?");
                p.setString(1, ISBN);
                p.setString(2, oldISBN);
                p.execute();
            }
            if (!ISBN.equals("") && !newauthor.equals("") && !newtitle.equals("")) {
                PreparedStatement p = conn.prepareStatement("UPDATE BOOK SET ISBN=?, AUTHOR=?, COMMENTS =?, TITLE=? WHERE ISBN=?");
                p.setString(1, ISBN);
                p.setString(2, newauthor);
                p.setInt(3, c);
                p.setString(4, newtitle);
                p.setString(5, oldISBN);
                p.execute();
            }
            else if (!ISBN.equals("") && !newauthor.equals("") && newtitle.equals("")) {
                PreparedStatement p = conn.prepareStatement("UPDATE BOOK SET ISBN=?, AUTHOR=?, COMMENTS =? WHERE ISBN=?");
                p.setString(1, ISBN);
                p.setString(2, newauthor);
                p.setInt(3, c);
                p.setString(4, oldISBN);
                p.execute();

            }
            else if (!ISBN.equals("") && newauthor.equals("") && newtitle.equals("")) {
                PreparedStatement p = conn.prepareStatement("UPDATE BOOK SET ISBN=? WHERE ISBN=?");
                p.setString(1, ISBN);
                p.setString(2, oldISBN);
                p.execute();

            }
            else if (!ISBN.equals("") && newauthor.equals("") && !newtitle.equals("")) {
                PreparedStatement p = conn.prepareStatement("UPDATE BOOK SET ISBN=?, COMMENTS =?, TITLE=? WHERE ISBN=?");
                p.setString(1, ISBN);
                p.setInt(2, c);
                p.setString(3, newtitle);
                p.setString(4, oldISBN);
                p.execute();

            }
            else if (ISBN.equals("") && !newauthor.equals("") && newtitle.equals("")) {
                PreparedStatement p = conn.prepareStatement("UPDATE BOOK SET AUTHOR=?, COMMENTS =? WHERE ISBN=?");
                p.setString(1, newauthor);
                p.setInt(2, c);
                p.setString(3, oldISBN);
                p.execute();
            }
            else if (ISBN.equals("") && newauthor.equals("") && !newtitle.equals("")) {
                PreparedStatement p = conn.prepareStatement("UPDATE BOOK SET COMMENTS =?, TITLE=? WHERE ISBN=?");
                p.setInt(1, c);
                p.setString(2, newtitle);
                p.setString(3, oldISBN);
                p.execute();
            }
            else if (ISBN.equals("") && !newauthor.equals("") && !newtitle.equals("")) {
                PreparedStatement p = conn.prepareStatement("UPDATE BOOK SET AUTHOR=?, COMMENTS =?, TITLE=? WHERE ISBN=?");
                p.setString(1, newauthor);
                p.setInt(2, c);
                p.setString(3, newtitle);
                p.setString(4, oldISBN);
                p.execute();
            }
            else {
            	System.out.println("nothing changed");
            }
            conn.close();
        }
        return drawHomePage();
    }

    @RequestMapping("addcomment")
    public ModelAndView addcomment(@RequestParam String newComment) throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:navin", "sa", "");
        ModelAndView mv = new ModelAndView("editbook.html");
        if(ifEntryExists(oldIsbn)) {
            Statement st = conn.createStatement();
            ResultSet rs;
            //count all comments to find ID
            rs = st.executeQuery("SELECT COUNT (COMMENT) FROM COMMENT");
            int id = 0;
            while (rs.next()) {
                id = rs.getInt(1) + 1;
            }
            LocalDateTime now = LocalDateTime.now();
            String year = String.valueOf(now.getYear());
            String month = String.valueOf(now.getMonthValue());
            String day = String.valueOf(now.getDayOfMonth());
            System.out.println(day.length());
            if (month.length() == 1) {
                month = "0" + month;
            }
            String date1 = year + month + day;

            //INSERT
            System.out.println(oldIsbn + "old isbn in add c");
            PreparedStatement p = conn.prepareStatement("INSERT INTO COMMENT VALUES(?, ?, ?, ?)");
            p.setInt(1, id);
            p.setString(2, newComment);
            p.setString(3, date1);
            p.setString(4, oldIsbn);
            p.execute();

            ArrayList<comment> arr = new ArrayList<>();
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM COMMENT WHERE ISBN='" + oldIsbn + "'");
            while (rs.next()) {
                comment b = new comment();
                b.setIsbn(rs.getString(4));
                System.out.println(rs.getString(1) + rs.getString(2) + rs.getString(3) + rs.getString(4));
                String d = rs.getString(3);
                String date = d.substring(0, 4) + "." + d.substring(4, 6) + "." + d.substring(6, 8);
                b.setDate(date);
                b.setId(rs.getInt(1));
                b.setComment(rs.getString(2));
                System.out.println(b.toString());
                arr.add(b);
            }
            mv.addObject("num", oldIsbn);
            mv.addObject("m", arr);
        }
        conn.close();
        return mv;
    }

    public void refreshData() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:navin","sa","");
        Statement st=conn.createStatement();
        ArrayList<String> arr=new ArrayList<>();
        ResultSet rs = st.executeQuery("SELECT * FROM BOOK");
        while (rs.next()) {
            arr.add(rs.getString(1));
        }
        for (int i = 0; i < arr.size(); i++) {
            int c=0;
            rs = st.executeQuery("SELECT COUNT (COMMENT) FROM COMMENT WHERE ISBN='" + arr.get(i) + "'");
            while (rs.next()) {
                c= rs.getInt(1);
            }
            st.execute("UPDATE BOOK SET COMMENTS='"+c+"' WHERE ISBN='"+ arr.get(i) + "'");
        }
        conn.close();
    }
    
    public boolean ifEntryExists(String id) throws ClassNotFoundException, SQLException {

        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:navin","sa","");
        Statement st=conn.createStatement();
        int c=0;
        ResultSet rs = st.executeQuery("SELECT * FROM BOOK WHERE ISBN='"+id+"'");
        while (rs.next()) {
            c++;
        }
        conn.close();
        if(c>0)return true;
        else return false;
    }

    public ModelAndView drawHomePage() throws SQLException, ClassNotFoundException {
        ModelAndView mv= new ModelAndView("home.html");
        ArrayList<book> arr=new ArrayList<>();
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:navin","sa","");
        Statement st=conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM BOOK");
        while (rs.next()) {
            book b = new book();
            b.setIsbn(rs.getString(1));
            b.setAuthor(rs.getString(2));
            b.setComments(rs.getInt(3));
            b.setTitle(rs.getString(4));
            arr.add(b);
        }
        mv.addObject("m",arr);
        conn.close();
        return mv;
    }

}


