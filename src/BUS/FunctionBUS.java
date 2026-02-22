package BUS;

import DAO.FunctionDAO;
import DTO.FunctionDTO;
import java.util.ArrayList;

public class FunctionBUS {
    private FunctionDAO functionDAO = new FunctionDAO();

    public ArrayList<FunctionDTO> getAll() {
        return functionDAO.getAll();
    }
}