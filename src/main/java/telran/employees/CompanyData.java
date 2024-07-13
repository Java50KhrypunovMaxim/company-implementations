package telran.employees;

import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

public class CompanyData implements Serializable {

    TreeMap<Long, Employee> employees;
    TreeMap<String, List<Employee>> employeesDepartment;
    TreeMap<Float, List<Manager>> factorManagers;

    public CompanyData(TreeMap<Long, Employee> employees, TreeMap<String, List<Employee>> employeesDepartment,
                       TreeMap<Float, List<Manager>> factorManagers) {
        this.employees = employees;
        this.employeesDepartment = employeesDepartment;
        this.factorManagers = factorManagers;
    }

    public TreeMap<Long, Employee> getEmployees() {
        return employees;
    }

    public TreeMap<String, List<Employee>> getEmployeesDepartment() {
        return employeesDepartment;
    }

    public TreeMap<Float, List<Manager>> getFactorManagers() {
        return factorManagers;
    }
}
