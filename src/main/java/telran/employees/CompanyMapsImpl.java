package telran.employees;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import telran.io.Persistable;

public class CompanyMapsImpl implements Company, Persistable {
	TreeMap<Long, Employee> employees = new TreeMap<>();
	TreeMap<String, List<Employee>> employeesDepartment = new TreeMap<>();
	TreeMap<Float, List<Manager>> factorManagers = new TreeMap<>();
	private class CompanyIterator implements Iterator<Employee>{
		Iterator<Employee> iterator = employees.values().iterator();
		Employee prev;
		@Override
		public boolean hasNext() {
			
			return iterator.hasNext();
		}

		@Override
		public Employee next() {
			prev = iterator.next();
			return prev;
		}
		@Override
		public void remove() {
			iterator.remove();
			removeFromIndexMaps(prev);
		}
		
	}
	@Override
	public Iterator<Employee> iterator() {
		
		return new CompanyIterator();
	}

	@Override
	public void addEmployee(Employee empl) {
		if (employees.putIfAbsent(empl.getId(), empl) != null) {
			throw new IllegalStateException();
		}
		addToIndexMap(employeesDepartment, empl.getDepartment(), empl);
		if (empl instanceof Manager) {
			Manager manager = (Manager)empl;
			addToIndexMap(factorManagers, manager.factor, manager);
		}

	}

	@Override
	public Employee getEmployee(long id) {
		
		return employees.get(id);
	}

	@Override
	public Employee removeEmployee(long id) {
		Employee empl = employees.remove(id);
		if(empl == null) {
			throw new NoSuchElementException();
		}
		removeFromIndexMaps(empl);
		return empl;
	}

	private void removeFromIndexMaps(Employee empl) {
		removeFromIndexMap(employeesDepartment, empl.getDepartment(), empl);
		if(empl instanceof Manager) {
			Manager manager = (Manager)empl;
			removeFromIndexMap(factorManagers, manager.factor, manager);
		}
	}
	private <K, V extends Employee> void removeFromIndexMap(Map<K, List<V>> map, K key, V empl) {
		map.computeIfPresent(key, (k, v) -> {
			v.remove(empl);
			return v.isEmpty() ? null : v;
		});
		
	}
	private <K, V extends Employee> void addToIndexMap(Map<K, List<V>> map, K key, V empl) {
		map.computeIfAbsent(key, k -> new ArrayList<>()).add(empl);
		
	}

	@Override
	public int getDepartmentBudget(String department) {
		return employeesDepartment.getOrDefault(department,
				Collections.emptyList()).stream()
		.collect(Collectors.summingInt(Employee::computeSalary));
	}

	@Override
	public String[] getDepartments() {
		return employeesDepartment.keySet()
				.toArray(String[]::new);
	}

	@Override
	public Manager[] getManagersWithMostFactor() {
		Manager[] res = new Manager[0];
		if(!factorManagers.isEmpty()) {
			res = factorManagers.lastEntry().getValue().toArray(res);
		}
		return res;
	}

	@Override
	public void save(String filePathStr) {

		   try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePathStr))) {
	            for (Employee employee : employees.values()) {
	                writer.write(employee.getJSON());
	        }}
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	}

	
	 @Override
	    public void restore(String filePathStr) {
	        try (BufferedReader reader = new BufferedReader(new FileReader(filePathStr))) {
	            JSONParser jsonParser = new JSONParser();
	            JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);
	            employees.clear();
	            employeesDepartment.clear();
	            factorManagers.clear();
	            for (Object obj : jsonArray) {
	                JSONObject jsonObject = (JSONObject) obj;
	                long id = (long) jsonObject.get("id");
	                int basicSalary = ((Long) jsonObject.get("basicSalary")).intValue(); 
	                String department = (String) jsonObject.get("department");

	                Employee employee = new Employee(id, basicSalary, department);
	                addEmployee(employee); 
	            }
	        } catch (IOException | ParseException e) {
	            e.printStackTrace();
	        }
	    }

    }
	    

	


