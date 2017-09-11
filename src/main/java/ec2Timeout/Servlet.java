/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec2Timeout;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ghsmith
 */
@WebServlet(name = "Servlet", urlPatterns = {"/main"})
public class Servlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String instanceId = (String)getServletContext().getAttribute("ec2InstanceId");
        
        if(request.getParameter("extend") != null) {
            int extend = Integer.parseInt(request.getParameter("extend"));
            if(extend == 240)) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, extend);
                getServletContext().setAttribute("ec2ShutdownTime", cal.getTime());
                if("stopped".equals(Ec2.getInstanceState(instanceId))) {
                    Ec2.startInstance(instanceId);
                    ((ArrayList)getServletContext().getAttribute("startStopLog")).add(Calendar.getInstance().getTime() + ": started");
                    try { Thread.sleep(5000); } catch(InterruptedException ex) { }
                }
            }
        }

        Date currentTime = Calendar.getInstance().getTime();
        Date ec2ShutdownTime = (Date)getServletContext().getAttribute("ec2ShutdownTime");
        String ec2InstanceState = Ec2.getInstanceState(instanceId);

        ArrayList startStopLog = ((ArrayList)getServletContext().getAttribute("startStopLog"));
        StringBuffer startStopLogText = new StringBuffer();
        for(int x = startStopLog.size() - 1; x >= 0; x--) {
            startStopLogText.append(startStopLog.get(x));
            startStopLogText.append("<br/>");
        }
        
        try {
            
            /* TODO output your page here. You may use following sample code. */
            if("running".equals(ec2InstanceState)) {

                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<meta http-equiv='refresh' content='10;URL=" + request.getContextPath() + "/main' />");
                out.println("<title>EC2 Timeout</title>");            
                out.println("</head>");
                out.println("<body style='font-family: courier'><table>");
                out.println("<tr><td>EC2 instance state:</td><td>" + ec2InstanceState + "</td></tr>");
                out.println("<tr><td>current time:</td><td>" + currentTime + "</td></tr>");
                out.println("<tr><td>next shutdown time:</td><td>" + ec2ShutdownTime + "</td></tr>");
                out.println("</table><form><input type='button' value='extend shutdown time to 4 hours from now' onclick='window.location.href=\"" + request.getContextPath() + "/main?extend=240\"' style='font-family: courier')/></form>");
                out.println("<p>Activity Log</p>");
                out.println("<p>" + startStopLogText + "</p>");
                out.println("</body>");
                out.println("</html>");

            }
            else if("stopped".equals(ec2InstanceState)) {

                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<meta http-equiv='refresh' content='10;URL=" + request.getContextPath() + "/main' />");
                out.println("<title>EC2 Timeout</title>");            
                out.println("</head>");
                out.println("<body style='font-family: courier'><table>");
                out.println("<tr><td>EC2 instance state:</td><td>" + ec2InstanceState + "</td></tr>");
                out.println("<tr><td>current time:</td><td>" + currentTime + "</td></tr>");
                out.println("<tr><td>next shutdown time:</td><td>" + "instance not running" + "</td></tr>");
                out.println("</table><form><input type='button' value='start with shutdown time 4 hours from now' onclick='window.location.href=\"" + request.getContextPath() + "/main?extend=240\"' style='font-family: courier')/></form>");
                out.println("<p>&nbsp;</p>");
                out.println("<p>Activity Log</p>");
                out.println("<p>" + startStopLogText + "</p>");
                out.println("</body>");
                out.println("</html>");

            }
            else {

                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<meta http-equiv='refresh' content='10;URL=" + request.getContextPath() + "/main' />");
                out.println("<title>EC2 Timeout</title>");            
                out.println("</head>");
                out.println("<body style='font-family: courier'><table>");
                out.println("<tr><td>EC2 instance state:</td><td>" + ec2InstanceState + "</td></tr>");
                out.println("<tr><td>current time:</td><td>" + currentTime + "</td></tr>");
                out.println("<tr><td>next shutdown time:</td><td>" + "instance not running" + "</td></tr>");
                out.println("</table><form><input type='button' value='refresh' onclick='window.location.href=\"" + request.getContextPath() + "/main\"' style='font-family: courier')/></form>");
                out.println("<p>&nbsp;</p>");
                out.println("<p>Activity Log</p>");
                out.println("<p>" + startStopLogText + "</p>");
                out.println("</body>");
                out.println("</html>");

            }
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
