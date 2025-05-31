using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Windows.Xps.Packaging;
using System.IO;
using System.Windows.Xps;
using System.Runtime.Serialization.Formatters.Binary;
using System.Windows.Markup;

namespace CheckApplication
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        ObservableCollection<Employee> observableEmployees = new ObservableCollection<Employee>();

        public MainWindow()
        {
            InitializeComponent();
            Load();
            EmployeeCmboBox.ItemsSource = observableEmployees;
            TaxListView.ItemsSource = Deductions.taxes;
        }

        private void Enable_And_Make_Visible_This_Panel_Only(string nameOfPanel)
        {
            /*
             * If a grid a property Name that is equal to nameOfGrid
             * it will make that grid the only visible and enabled grid
             *  
             * The reason this is done is to decrease the number of places
             * that code can actually be triggered from so there are less
             * places to cause a problem.
             */
            var panels = ContentGrid.Children.OfType<Panel>();
            foreach (var panel in panels)
            {
                if(panel.Name.Equals(nameOfPanel))
                {
                    panel.IsEnabled = true;
                    panel.Visibility = Visibility.Visible;
                }
                else
                {
                    panel.IsEnabled = false;
                    panel.Visibility = Visibility.Collapsed;
                }
            }
        }

        private void buttonAddEmployee_Click(object sender, RoutedEventArgs e)
        {
            //Replace with a Key-Value Pair next time so that I can use names instead of numbers to make it easier to update the code if I need to add entries
            TextBox[] textBoxes = new TextBox[] { txtFirstName, txtMiddle, txtLastName, txtSSN };
            observableEmployees.Add(new Employee(textBoxes[0].Text, textBoxes[1].Text, textBoxes[2].Text, textBoxes[3].Text));
            
            //Reset the person grid back to its previous state.
            //Potential solution is to save the inner xml as a string in memmory and set it to the variable anytime the grid is activated.
            buttonAddEmployee.IsEnabled = false;
            foreach(var textBox in textBoxes)
            {
                textBox.Text = "";
            }
            
            Enable_And_Make_Visible_This_Panel_Only("CheckGrid");
        }

        private void Button_Click(object sender, RoutedEventArgs e) => Enable_And_Make_Visible_This_Panel_Only("PersonGrid");

        private void ShouldAddPersonButtonBeActive(object sender, EventArgs e)
        {
            //Add a new employee once every textbox has been completed
            string[] texts = new string[] { txtFirstName.Text, txtMiddle.Text, txtLastName.Text, txtSSN.Text,txtAddress1.Text,txtAddress2.Text };
            foreach (var text in texts)
            {
                if(text.Equals(""))
                {
                    buttonAddEmployee.IsEnabled = false;
                    return;
                }
            }
            buttonAddEmployee.IsEnabled = true;
        }

        private void PeriodButton_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                Period temp = new Period()
                {
                    Begin = (DateTime)StartDate.SelectedDate,
                    End = (DateTime)EndDate.SelectedDate,
                    HoursWorked = int.Parse(HourTxtBox.Text),
                    OvertimeHoursWorked = int.Parse(OvertimeTxtBox.Text),
                    HourlyRate = int.Parse(HourRateTxtBox.Text),
                    OvertimeRate = int.Parse(OvertimeRateTxtBox.Text)
                };
                ((Employee)EmployeeCmboBox.SelectedItem).AddPeriod(temp);
                FixedDocument_Loaded(sender, e);
            }
            catch
            {
                MessageBox.Show("There is a problem with the information provided.");
            }
            finally
            {
                StartDate.SelectedDate = null;
                EndDate.SelectedDate = null;
                HourTxtBox.Text = "";
                OvertimeTxtBox.Text = "";
                HourRateTxtBox.Text = "";
                OvertimeRateTxtBox.Text = "";
            }
        }

        private void EmployeeCmboBox_SelectionChanged(object sender, SelectionChangedEventArgs e) => PeriodGrid.IsEnabled = (EmployeeCmboBox.SelectedIndex >= 0);

        private void Save(object sender, EventArgs e)
        {
            BinaryFormatter formatter = new BinaryFormatter();
            Data data = new Data
            {
                oCollect = observableEmployees,
                taxes = Deductions.taxes,
                SocialSecurityPercentage = Deductions.SocialSecurityPercentage,
                MedicarePercentage = Deductions.MedicarePercentage
            };
            using (var stream = new FileStream("data.bin", FileMode.Create))
            {
                formatter.Serialize(stream, data);
            }
        }

        private void Load()
        {
            if (File.Exists("data.bin")) 
            {
                BinaryFormatter formatter = new BinaryFormatter();
                using (var stream = new FileStream("data.bin", FileMode.Open))
                {
                    var data = (Data)formatter.Deserialize(stream);
                    observableEmployees = data.oCollect == null ? new ObservableCollection<Employee>() : data.oCollect;
                    Deductions.taxes = data.taxes == null ? new ObservableCollection<(decimal max, decimal taxRate)>() : data.taxes;
                    Deductions.SocialSecurityPercentage = data.SocialSecurityPercentage;
                    Deductions.MedicarePercentage = data.MedicarePercentage;
                    SSNPercentageTxtbox.Text = Deductions.SocialSecurityPercentage.ToString();
                    MedicarePercentageTxtbox.Text = Deductions.MedicarePercentage.ToString();
                }
            }
        }

        private void StartDate_SelectedDateChanged(object sender, SelectionChangedEventArgs e) => EndDate.SelectedDate = StartDate.SelectedDate.Equals(null) ? new DateTime() : StartDate.SelectedDate.Value.AddDays(14);

        private void AddTaxButton_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                var income = Decimal.Parse(FederalIcomeTxtbox.Text);
                var tax = Decimal.Parse(FederalTaxRateTxtbox.Text);
                Deductions.taxes.Add((income, tax));
            }
            catch
            {
                MessageBox.Show("There is a problem with the inputs.");
            }
        }

        private void RemoveTaxButton_Click(object sender, RoutedEventArgs e)
        {
            if(TaxListView.SelectedIndex != -1)
            {
                Deductions.taxes.RemoveAt(TaxListView.SelectedIndex);
            }
        }

        private void Button_Click_1(object sender, RoutedEventArgs e) => Enable_And_Make_Visible_This_Panel_Only("CheckGrid");

        private void Button_Click_2(object sender, RoutedEventArgs e) => Enable_And_Make_Visible_This_Panel_Only("DeductionStackPanel");

        private void FixedDocument_Loaded(object sender, RoutedEventArgs e)
        {
            FixedDocument fixedDocument = DocFD;
            UserControl myUserControl = new UserControl();
            myUserControl.HorizontalAlignment = HorizontalAlignment.Center;
            myUserControl.VerticalAlignment = VerticalAlignment.Center;

            Grid grid = new Grid();
            grid.Children.Add(myUserControl);

            FixedPage fixedPage = new FixedPage();
            fixedPage.Children.Add(grid);

            

            List<(double, double, string)> textInformation = new List<(double, double, string)>();
            var emp = ((Employee)EmployeeCmboBox.SelectedItem);
            var cur = emp.LatestPeriod;
            //This was auto generated using a powershell script. This was needed since an issue with using FixedDocuments in Visual Studio prevents from accessing any of it children without causing errors.
            textInformation.Add((100, 100, emp.Name));
            textInformation.Add((675, 118, String.Format("{0:.00}", cur.PostTaxIncome)));
            textInformation.Add((100, 150, ConvertToLongName(cur.PostTaxIncome,true)));
            textInformation.Add((100, 200, emp.Address1));
            textInformation.Add((100, 250, emp.Address2));
            //textInformation.Add((500, 247.5, "Signature"));

            textInformation.Add((15, 400, $"Period Begin: {cur.Begin.ToShortDateString()}"));
            textInformation.Add((300, 400, $"Period End: {cur.End.ToShortDateString()}"));
            textInformation.Add((15, 450, $"Today's Date: {DateTime.Now.ToShortDateString()}"));
            textInformation.Add((15, 500, $"Employee Name: {emp.Name}"));
            textInformation.Add((300, 500, $"Social Security: {emp.SocialSecurityNumber}"));

            textInformation.Add((15, 550, "Reg Hour"));
            textInformation.Add((100, 550, "Rate"));
            textInformation.Add((175, 550, "Total"));
            textInformation.Add((225, 550, "Overtime"));
            textInformation.Add((300, 550, "Rate"));
            textInformation.Add((350, 550, "Total"));
            textInformation.Add((400, 550, "Paid"));
            textInformation.Add((450, 550, "Total Deduc."));
            textInformation.Add((550, 550, "Paid"));
            textInformation.Add((650, 550, "Total YTD"));

            textInformation.Add((15, 565, cur.HoursWorked.ToString()));
            textInformation.Add((100, 565, cur.HourlyRate.ToString()));
            textInformation.Add((175, 565, String.Format("${0:.00}", cur.Pay)));
            textInformation.Add((225, 565, cur.OvertimeHoursWorked.ToString()));
            textInformation.Add((300, 565, cur.OvertimeRate.ToString()));
            textInformation.Add((350, 565, String.Format("${0:.00}", cur.OvertimePay)));
            textInformation.Add((400, 565, String.Format("${0:.00}", cur.PreTaxIncome)));

            textInformation.Add((15, 580, "YTD"));
            textInformation.Add((175, 580, "YTD"));
            textInformation.Add((225, 580, "YTD"));
            textInformation.Add((450, 580, "YTD"));

            textInformation.Add((15, 595, emp.TotalHoursWorked.ToString()));
            textInformation.Add((175, 595, String.Format("${0:.00}", emp.TotalHoursWorked)));
            textInformation.Add((225, 595, emp.TotalOvertimeHoursWorked.ToString()));
            textInformation.Add((350, 595, String.Format("${0:.00}", emp.TotalOvertimePay)));
            textInformation.Add((550, 595, String.Format("${0:.00}", cur.PostTaxIncome)));
            textInformation.Add((650, 595, String.Format("${0:.00}", emp.TotalPostTaxIncome)));


            textInformation.Add((450, 610, String.Format("${0:.00}", cur.CombinedDeductions)));
            textInformation.Add((650, 610, String.Format("${0:.00}", emp.TotalDeductions)));

            textInformation.Add((15, 625, "Social Security"));
            textInformation.Add((450, 625, String.Format("${0:.00}", cur.SocialSecurity())));
            textInformation.Add((650, 625, String.Format("${0:.00}", emp.TotalSSN)));

            textInformation.Add((15, 640, "Federal Medicare"));
            textInformation.Add((450, 640, String.Format("${0:.00}", cur.Medicare())));
            textInformation.Add((650, 640, String.Format("${0:.00}", emp.TotalMedicare)));

            textInformation.Add((15, 655, "Federal Tax"));
            textInformation.Add((450, 655, String.Format("${0:.00}", cur.FederalIncomeTax())));
            textInformation.Add((650, 655, String.Format("${0:.00}", emp.TotalFed)));

            foreach(var info in textInformation)
            {
                TextBox t = new TextBox();
                t.Margin = new Thickness(info.Item1, info.Item2, 0, 0);
                t.Text = info.Item3;
                fixedPage.Children.Add(t);
            }

            
            Binding widthBinding = new Binding("ActualWidth");
            widthBinding.Source = fixedPage;
            Binding heightBinding = new Binding("ActualHeight");
            heightBinding.Source = fixedPage;
            grid.SetBinding(Grid.WidthProperty, widthBinding);
            grid.SetBinding(Grid.HeightProperty, heightBinding);

            PageContent pageContent = new PageContent();
            
            (pageContent as IAddChild).AddChild(fixedPage);

            fixedDocument.Pages.Add(pageContent);
        }

        private void MedicarePercentageTxtbox_TextChanged(object sender, KeyboardFocusChangedEventArgs e)
        {
            try 
            {
                var potential = MedicarePercentageTxtbox.Text;
                Deductions.MedicarePercentage = Decimal.Parse(potential);
            }
            catch { MessageBox.Show("There is a problem with the Medicare Percentage issue."); }
        }

        private void SSNPercentageTxtbox_LostKeyboardFocus(object sender, KeyboardFocusChangedEventArgs e)
        {
            try
            {
                var potential = SSNPercentageTxtbox.Text;
                Deductions.SocialSecurityPercentage = Decimal.Parse(potential);
            }
            catch { MessageBox.Show("There is a problem with the Social Security Percentage issue."); }
        }

        private string ConvertToLongName(Decimal d, bool checkDecimalPlace = false, int digitLocation = 0)
        {
            if (d < 0)
            {
                MessageBox.Show("negative infintiy dollars! Positive Real Numbers Only");
                return "negative infinity moneys";
            }
            else if (d < 0.01M)
            {
                return "";
            }
            else if (d >= 1000000)
            {
                throw new Exception("Can't convert values greater than or equal to 1 million. Please enter lower values");
            }

            string[] ones = new string[] { "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine" };
            string[] tens = new string[] { "ten", "eleven", "tweleve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eightteen", "nineteen" };
            string[] tensPlace = new string[] { "twenty", "thirty", "fourty", "fifty", "sixty", "seventy", "eighty", "ninety" };
            string[] decimalPlace = new string[] { "", "thousand " }; //empty string is neccessary for values less than one thousand

            string value = "";

            int temp = (int)(d % 1000);
            int hundred = (temp / 100); //Keeps only the value at the hundreds place

            int temp2 = temp - hundred * 100;
            if (hundred < 10 && hundred > 0)
            {
                value += (temp2 == 0) ? $"{ones[hundred]} hundred " : $"{ones[hundred]} hundred and ";
            }
            int temp3 = temp2;
            if (temp2 < 20 && temp2 >= 10)
            {
                value += $"{tens[temp2 - 10]} ";
                temp3 = 0;
            }
            else if (temp2 >= 20)
            {
                temp3 = temp2 % 10; //keeps only the value of the ones place
                temp2 = temp2 / 10; //keeps the values of the tens place
                value += (temp3 == 0) ? $"{tensPlace[temp2 - 2]} " : $"{tensPlace[temp2 - 2]} - ";
            }

            if (temp3 > 0)
            {
                value += $"{ones[temp3]} ";
            }

            string cents = "";
            if (checkDecimalPlace)
            {
                temp2 = (int)((d % 1) * 100);
                temp3 = temp2;
                if (temp2 < 20 && temp2 >= 10)
                {
                    cents += $"{tens[temp2 - 10]} ";
                    temp3 = 0;
                }
                else if (temp2 >= 20)
                {
                    temp3 = temp2 % 10; //keeps only the value of the ones place
                    temp2 = temp2 / 10; //keeps the values of the tens place
                    cents += (temp3 == 0) ? $"{tensPlace[temp2 - 2]} " : $"{tensPlace[temp2 - 2]} - ";
                }

                if (temp3 == 0 && temp2 > 0)
                {
                    cents += "";
                }
                else if (temp3 >= 0 && temp2 >= 0)
                {
                    cents += $"{ones[temp3]} ";
                }
            }

            if (digitLocation == 0)
            {
                return ConvertToLongName((decimal)((int)(d / 1000)), digitLocation: digitLocation + 1) + value + $"{decimalPlace[digitLocation]}dollars" + $" and {cents}cents";
            }
            return ConvertToLongName((decimal)((int)(d / 1000)), digitLocation: digitLocation + 1) + value + $"{decimalPlace[digitLocation]}";
        }
    }

    [Serializable]
    public struct Data
    {
        public ObservableCollection<Employee> oCollect;
        public ObservableCollection<(Decimal max, Decimal taxRate)> taxes;
        public Decimal MedicarePercentage { get; set; }
        public Decimal SocialSecurityPercentage { get; set; }
    }

    //Let user set the tax rate per year
    [Serializable]
    public static class Deductions
    {
        public static ObservableCollection<(Decimal max, Decimal taxRate)> taxes = new ObservableCollection<(Decimal max, Decimal taxRate)>();
        public static ObservableCollection<(Decimal max, Decimal taxRate)>[] form = new ObservableCollection<(decimal max, decimal taxRate)>[5];
        public static Decimal MedicarePercentage { get; set; }
        public static Decimal SocialSecurityPercentage { get; set; }

        public static Decimal PreTaxIncome {private get; set; }
        
        public static Decimal FederalIncomeTaxes { get => StartCalculateTaxes(); }
        public static Decimal Medicare => PreTaxIncome * MedicarePercentage;
        public static Decimal SocialSecurity => PreTaxIncome * SocialSecurityPercentage;

        private static Decimal StartCalculateTaxes()
        {
            var taxs = taxes.GetEnumerator();
            taxs.MoveNext();
            Decimal maxBeforTaxRateChange = taxs.Current.max;
            Decimal taxRate = taxs.Current.taxRate;
            Decimal difference = PreTaxIncome - maxBeforTaxRateChange;
            return difference >= 0 ? RecursiveCalculateTaxes(PreTaxIncome,taxs) : 0;
        }
        private static Decimal RecursiveCalculateTaxes(Decimal income, IEnumerator<(Decimal max, Decimal taxRate)> taxes)
        {
            Decimal maxBeforTaxRateChange = taxes.Current.max;
            Decimal taxRate = taxes.Current.taxRate;

            taxes.MoveNext();

            Decimal difference = income - maxBeforTaxRateChange;
            if(difference > 0)
            {
                return maxBeforTaxRateChange * taxRate + RecursiveCalculateTaxes(difference, taxes);
            }
            return income * taxRate;
        }
    }

    [Serializable]
    public struct Period
    {
        public DateTime Begin { get; set; }
        public DateTime End { get; set; }

        public Decimal HoursWorked { get; set; }
        public Decimal OvertimeHoursWorked { get; set; }
        public Decimal HourlyRate { get; set; }
        public Decimal OvertimeRate { get; set; }

        public Decimal Pay { get => HoursWorked * HourlyRate; }
        public Decimal OvertimePay { get => OvertimeHoursWorked * OvertimeRate; }
        public Decimal PreTaxIncome { get => Pay + OvertimePay; }

        public Decimal PostTaxIncome => PreTaxIncome - CombinedDeductions;
        public Decimal CombinedDeductions => FederalIncomeTax() + Medicare() + SocialSecurity();
        public Decimal FederalIncomeTax()
        {
            Deductions.PreTaxIncome = PreTaxIncome;
            return Deductions.FederalIncomeTaxes;
        }

        public Decimal Medicare()
        {
            Deductions.PreTaxIncome = PreTaxIncome;
            return Deductions.Medicare;
        }

        public Decimal SocialSecurity()
        {
            Deductions.PreTaxIncome = PreTaxIncome;
            return Deductions.SocialSecurity;
        }
    }

    [Serializable]
    public class Employee
    {
        string FirstName { get; }
        string MiddleName { get; }
        string LastName { get; }

        public string Name { get => $"{FirstName} {MiddleName} {LastName}"; }
        public string SocialSecurityNumber { get; }
        public string Address1;
        public string Address2;

        //Dictionary was used to make sure that there were no duplicate dates as the starting period of the check will always be unique
        readonly Dictionary<DateTime,Period> periods = new Dictionary<DateTime, Period>();

        public Employee(string firstName, string middleName, string lastName, string socialSecurityNumber)
        {
            FirstName = firstName;
            MiddleName = middleName;
            LastName = lastName;
            SocialSecurityNumber = socialSecurityNumber;
        }

        public Decimal TotalIncome => (from period in periods.Values select period.PostTaxIncome).Sum();
        public Decimal TotalHoursWorked => (from period in periods.Values select period.HoursWorked).Sum();
        public Decimal TotalPay => (from period in periods.Values select period.Pay).Sum();
        public Decimal TotalOvertimeHoursWorked => (from period in periods.Values select period.OvertimeHoursWorked).Sum();
        public Decimal TotalOvertimePay => (from period in periods.Values select period.OvertimePay).Sum();
        public Decimal TotalPostTaxIncome => (from period in periods.Values select period.PostTaxIncome).Sum();
        public Decimal TotalDeductions => (from period in periods.Values select period.CombinedDeductions).Sum();
        public Decimal TotalSSN => (from period in periods.Values select period.SocialSecurity()).Sum();
        public Decimal TotalMedicare => (from period in periods.Values select period.Medicare()).Sum();
        public Decimal TotalFed => (from period in periods.Values select period.FederalIncomeTax()).Sum();

        public void AddPeriod(Period period) { periods[period.Begin] = period; }

        public Period LatestPeriod => periods.Last().Value;

        public override string ToString() => Name;
    }
}
