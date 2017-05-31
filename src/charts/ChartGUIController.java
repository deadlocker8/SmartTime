package charts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import core.LogObject;
import core.SQL;
import core.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logger.Logger;
import tools.AlertGenerator;

@SuppressWarnings("rawtypes")
public class ChartGUIController
{
	@FXML private Button showButton;
	@FXML private ComboBox<String> projectBox;
	@FXML private ComboBox<String> taskBox;
	@FXML private ComboBox<String> yearBox;
	@FXML private ComboBox<String> monthBox;
	@FXML private AnchorPane chartPane;

	private Stage stage;
	private ArrayList<String> tasks;
	private ArrayList<String> years;
	private ArrayList<String> months;
	private SQL sql;
	private Image icon;
	private PieChartGenerator generator;
	private SummaryGenerator summaryGenerator;
	private BarChartGenerator barChartGenertator;	
	
	public void init(String savePath, Stage stage, Image icon)
	{
		try
		{
		    this.stage = stage;
			this.icon = icon;
			sql = new SQL(savePath);

			ArrayList<String> projects = new ArrayList<String>();
			projects.add("Alle Projekte");
			ArrayList<String> projectNames = sql.getProjectNames();
			projects.addAll(projectNames);
			Collections.sort(projects);

			projectBox.getItems().addAll(projects);
			projectBox.setValue(projects.get(0));
			projectBox.setStyle("-fx-font-family: \"Arial\";-fx-font-size: 15px;");
			taskBox.setStyle("-fx-font-family: \"Arial\";-fx-font-size: 15px;");
			yearBox.setStyle("-fx-font-family: \"Arial\";-fx-font-size: 15px;");
			monthBox.setStyle("-fx-font-family: \"Arial\";-fx-font-size: 15px;");

			generator = new PieChartGenerator(savePath);
			summaryGenerator = new SummaryGenerator(savePath);
			barChartGenertator = new BarChartGenerator(savePath);
			
			years = sql.getYears();
			Collections.sort(years);
			yearBox.getItems().clear();
			yearBox.getItems().add("Alle Jahre");
			yearBox.getItems().addAll(years);
			yearBox.getSelectionModel().select(0);
			showPieChart(generator.getChart0000("Alle Projekte"));

			projectBox.valueProperty().addListener(new ChangeListener<String>()
			{
				@Override
				public void changed(ObservableValue ov, String t, String t1)
				{
					String selectedProject = projectBox.getValue();
					if(selectedProject.equals("Alle Projekte"))
					{
						try
						{
							taskBox.getItems().clear();
							years = sql.getYears();
							Collections.sort(years);
							yearBox.getItems().clear();
							yearBox.getItems().add("Alle Jahre");
							yearBox.getItems().addAll(years);
							yearBox.getSelectionModel().select(0);
							monthBox.getItems().clear();

							showPieChart(generator.getChart0000("Alle Projekte"));
						}
						catch(Exception e)
						{
							Logger.error(e);
							showErrorMessage();
						}
					}
					else
					{
						try
						{
							tasks = getTasks(sql.getByProject(selectedProject));
							taskBox.getItems().clear();
							taskBox.getItems().add("Alle Tasks");
							Collections.sort(tasks);
							taskBox.getItems().addAll(tasks);
							taskBox.getSelectionModel().select(0);

							years = getYears(sql.getByProject(selectedProject));
							Collections.sort(years);
							yearBox.getItems().clear();
							yearBox.getItems().add("Alle Jahre");
							yearBox.getItems().addAll(years);
							yearBox.getSelectionModel().select(0);

							monthBox.getItems().clear();

							showPieChart(generator.getChart1000(selectedProject, selectedProject));
						}
						catch(Exception e)
						{
							Logger.error(e);
							showErrorMessage();
						}
					}
				}
			});

			taskBox.valueProperty().addListener(new ChangeListener<String>()
			{
				@Override
				public void changed(ObservableValue ov, String t, String t1)
				{

					String selectedProject = projectBox.getValue();
					String selectedTask = taskBox.getValue();

					if(selectedProject != null && selectedTask != null)
					{
						if( ! selectedProject.equals("Alle Projekte"))
						{
							if(selectedTask.equals("Alle Tasks"))
							{
								try
								{
									years = getYears(sql.getByProject(selectedProject));
									Collections.sort(years);
									yearBox.getItems().clear();
									yearBox.getItems().add("Alle Jahre");
									yearBox.getItems().addAll(years);
									yearBox.getSelectionModel().select(0);
									monthBox.getItems().clear();

									showPieChart(generator.getChart1000(selectedProject, selectedProject));
								}
								catch(Exception e)
								{
									Logger.error(e);
									showErrorMessage();
								}
							}
							else
							{
								try
								{
									years = getYears(sql.getByProjectAndTask(selectedProject, selectedTask));
									Collections.sort(years);
									yearBox.getItems().clear();
									yearBox.getItems().add("Alle Jahre");
									yearBox.getItems().addAll(years);
									yearBox.getSelectionModel().select(0);
									monthBox.getItems().clear();
										
									showSummary(summaryGenerator.getSummaryByProjectAndTask(selectedProject, selectedTask));
								}
								catch(Exception e)
								{
									Logger.error(e);
									showErrorMessage();
								}
							}
						}
					}
				}
			});

			yearBox.valueProperty().addListener(new ChangeListener<String>()
			{
				@Override
				public void changed(ObservableValue ov, String t, String t1)
				{
					String selectedProject = projectBox.getValue();
					String selectedTask = taskBox.getValue();
					String selectedYear = yearBox.getValue();

					if(selectedProject != null && selectedYear != null)
					{
						if(selectedProject.equals("Alle Projekte"))
						{
							if(selectedYear.equals("Alle Jahre"))
							{
								try
								{
									monthBox.getItems().clear();
									showPieChart(generator.getChart0000(selectedProject));
								}
								catch(Exception e)
								{
									Logger.error(e);
									showErrorMessage();
								}
							}
							else
							{
								try
								{								
									months = getMonths(sql.getLogObjects());
									monthBox.getItems().clear();
									monthBox.getItems().add("Alle Monate");
									monthBox.getItems().addAll(months);
									monthBox.getSelectionModel().select(0);

									showPieChart(generator.getChart0010(Integer.parseInt(selectedYear), "Alle Projekte - " + selectedYear));
								}
								catch(Exception e)
								{
									Logger.error(e);
									showErrorMessage();
								}
							}
						}
						else
						{
							if(selectedTask.equals("Alle Tasks"))
							{
								if(selectedYear.equals("Alle Jahre"))
								{
									try
									{
										monthBox.getItems().clear();
										showPieChart(generator.getChart1000(selectedProject, selectedProject));
									}
									catch(Exception e)
									{
										Logger.error(e);
										showErrorMessage();
									}
								}
								else
								{
									try
									{
										months = getMonths(sql.getByProjectAndYear(selectedProject, Integer.parseInt(selectedYear)));
										monthBox.getItems().clear();
										monthBox.getItems().add("Alle Monate");
										monthBox.getItems().addAll(months);
										monthBox.getSelectionModel().select(0);

										showPieChart(generator.getChart1010(selectedProject, Integer.parseInt(selectedYear), selectedProject + " - " + selectedYear));
									}
									catch(Exception e)
									{
										Logger.error(e);
										showErrorMessage();
									}
								}
							}
							else
							{
								if(selectedYear.equals("Alle Jahre"))
								{
									try
									{
										monthBox.getItems().clear();
										showSummary(summaryGenerator.getSummaryByProjectAndTask(selectedProject, selectedTask));
									}
									catch(Exception e)
									{
										Logger.error(e);
										showErrorMessage();
									}
								}
								else
								{
									try
									{
										months = getMonths(sql.getByProjectAndTaskAndYear(selectedProject, selectedTask, Integer.parseInt(selectedYear)));
										monthBox.getItems().clear();
										monthBox.getItems().add("Alle Monate");
										monthBox.getItems().addAll(months);
										monthBox.getSelectionModel().select(0);

										showSummary(summaryGenerator.getSummaryByProjectAndTaskAndYear(selectedProject, selectedTask, Integer.parseInt(selectedYear)));
									}
									catch(Exception e)
									{
										Logger.error(e);
										showErrorMessage();
									}
								}
							}
						}
					}
				}
			});

			monthBox.valueProperty().addListener(new ChangeListener<String>()
			{
				@Override
				public void changed(ObservableValue ov, String t, String t1)
				{
					String selectedProject = projectBox.getValue();
					String selectedTask = taskBox.getValue();
					String selectedYear = yearBox.getValue();
					String selectedMonth = monthBox.getValue();

					if(selectedProject != null && selectedYear != null && selectedMonth != null)
					{
						if(selectedProject.equals("Alle Projekte"))
						{
							if(!selectedYear.equals("Alle Jahre"))
							{
								if(selectedMonth.equals("Alle Monate"))
								{
									try
									{							
										showPieChart(generator.getChart0010(Integer.parseInt(selectedYear), "Alle Projekte - " + selectedYear));
									}								
									catch(Exception e)
									{
										Logger.error(e);
										showErrorMessage();
									}
								}
								else
								{
									try
									{
										showPieChart(generator.getChart0011(Integer.parseInt(selectedYear), Utils.getMonthNumber(selectedMonth), "Alle Projekte - " + selectedMonth + " " + selectedYear));
									}
									catch(Exception e)
									{
										Logger.error(e);
										showErrorMessage();
									}
								}
							}							
						}
						else
						{
							if(selectedTask.equals("Alle Tasks"))
							{
								if(!selectedYear.equals("Alle Jahre"))
								{
									if(selectedMonth.equals("Alle Monate"))
									{
										try
										{
											showPieChart(generator.getChart1010(selectedProject, Integer.parseInt(selectedYear), selectedProject + " - " + selectedYear));
										}
										catch(Exception e)
										{
											Logger.error(e);
											showErrorMessage();										
										}
									}
									else
									{
										try
										{
											showPieChart(generator.getChart1011(selectedProject, Integer.parseInt(selectedYear), Utils.getMonthNumber(selectedMonth), selectedProject + " - " + selectedMonth + " " + selectedYear));
										}
										catch(Exception e)
										{
											Logger.error(e);
											showErrorMessage();										
										}
									}
								}								
							}
							else
							{
								if(!selectedYear.equals("Alle Jahre"))
								{
									if(selectedMonth.equals("Alle Monate"))
									{
										try
										{
											showSummary(summaryGenerator.getSummaryByProjectAndTask(selectedProject, selectedTask));
										}
										catch(Exception e)
										{
											showErrorMessage();
											Logger.error(e);
										}
									}
									else
									{
										try
										{
											showBarChart(barChartGenertator.getBarChart(selectedProject, selectedTask, Integer.parseInt(selectedYear), Utils.getMonthNumber(selectedMonth)));
										}
										catch(Exception e)
										{
											showErrorMessage();
											Logger.error(e);
										}
									}
								}			
							}
						}
					}
				}
			});
		}
		catch(Exception e)
		{
			Logger.error(e);
			showErrorMessage();
		}
	}
	
	private void showErrorMessage()
	{
		AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", "Fehler beim Erstellen des Diagramms.", icon, stage, null, false);	
	}

	private void showPieChart(PieChart chart)
	{
		chartPane.getChildren().clear();
		chartPane.getChildren().add(chart);
		AnchorPane.setBottomAnchor(chart, 14.0);
		AnchorPane.setTopAnchor(chart, 0.0);
		AnchorPane.setLeftAnchor(chart, 14.0);
		AnchorPane.setRightAnchor(chart, 14.0);
		chartPane.setMaxHeight(Double.MAX_VALUE);
	}
	
	private void showSummary(VBox vboxSummary)
	{			
		chartPane.getChildren().clear();
		chartPane.getChildren().add(vboxSummary);
		AnchorPane.setBottomAnchor(vboxSummary, 14.0);
		AnchorPane.setTopAnchor(vboxSummary, 0.0);
		AnchorPane.setLeftAnchor(vboxSummary, 14.0);
		AnchorPane.setRightAnchor(vboxSummary, 14.0);
		chartPane.setMaxHeight(Double.MAX_VALUE);
	}

	private void showBarChart(BarChart<String, Number> chart)
	{
		chartPane.getChildren().clear();
		chartPane.getChildren().add(chart);
		AnchorPane.setBottomAnchor(chart, 14.0);
		AnchorPane.setTopAnchor(chart, 0.0);
		AnchorPane.setLeftAnchor(chart, 14.0);
		AnchorPane.setRightAnchor(chart, 14.0);
		chartPane.setMaxHeight(Double.MAX_VALUE);
	}
	
	private ArrayList<String> getTasks(ArrayList<LogObject> objects)
	{
		HashSet<String> tasks = new HashSet<String>();
		for(LogObject current : objects)
		{
			tasks.add(String.valueOf(current.getTask()));
		}
		return new ArrayList<String>(tasks);
	}

	private ArrayList<String> getYears(ArrayList<LogObject> objects)
	{
		HashSet<String> years = new HashSet<String>();
		for(LogObject current : objects)
		{
			years.add(String.valueOf(current.getYear()));
		}
		return new ArrayList<String>(years);
	}

	private ArrayList<String> getMonths(ArrayList<LogObject> objects)
	{
		HashSet<Integer> months = new HashSet<Integer>();
		for(LogObject current : objects)
		{
			months.add(current.getMonth());
		}

		ArrayList<String> monthNames = new ArrayList<String>();		

		for(int k = 0; k <  new ArrayList<Integer>(months).size(); k++)
		{
		    monthNames.add(Utils.getMonthName(k));
		}

		return monthNames;
	}	
}