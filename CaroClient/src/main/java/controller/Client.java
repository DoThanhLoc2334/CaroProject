/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import javax.swing.UIManager;
import model.Point;
import model.User;

/**
 *
 * @author hoang
 */
public class Client {
    public static void main(String[] args) {
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch(Exception e) { /* ignore */ }
        java.awt.EventQueue.invokeLater(() -> {
            new view.LoginFrm().setVisible(true);
        });
    }
}
