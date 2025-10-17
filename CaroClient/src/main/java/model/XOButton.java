/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import javax.swing.JButton;
/**
 *
 * @author hoang
 */
public class XOButton extends JButton{
    private int row;
    private int col;
    private int state;
    public XOButton(int row, int col){
        this.row = row;
        this.col = col;
        this.state = 0;
        
        this.setText("");
    }
    public int getRow(){return row;}
    public int getCol(){return col;}
    public int getState(){return state;}
    public void setState(int state){this.state=state;}
}
