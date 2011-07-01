using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Browser;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Json;
using System.IO;
using System.Text;


namespace iine_sample
{
    public partial class MainPage : UserControl
    {
        private HyperlinkButton[] hlButtons;
        public MainPage()
        {
            InitializeComponent();
        }

        private void UserControl_Loaded(object sender, RoutedEventArgs e)
        {

            HtmlPage.RegisterScriptableObject("SVL",this);


            /////////////////////////////////////////////
            
            var w = HtmlPage.Window;
            //////////////////////////////////
            var r = (ScriptObject)w.Invoke("xhrFunc");

            string v3 = Convert.ToString(w.GetProperty("view"));
 
            

        }
        [ScriptableMember()]
        public void ShowSVL(String response)
        {
            var w = HtmlPage.Window;
 
            JsonArray array = (JsonArray)JsonObject.Parse(response);

            var message = new StringBuilder();

            var members = from member in array
                          select member;

            this.hlButtons = new HyperlinkButton[array.Count];
            int i = 0;
            int[] Left = {285, 311, 294, 69, 161, 305, 86, 187, 294,  69, 55};
            int[] Top = { 167, 88, 128, 50, 265, 50, 204, 306, 224, 88, 167 };
            int[] FontSize = {26,22,22,16,16,16,16,16,16,16,16,16,16,16 };
            foreach (JsonObject member in members)
            {
                string Content = member["Content"];
                string NavigateUri = member["NavigateUri"];

                hlButtons[i] = new HyperlinkButton();
                hlButtons[i].Content = Content;
                hlButtons[i].Height = 35;
                hlButtons[i].Width = 230;
                hlButtons[i].NavigateUri = new Uri(NavigateUri);
                hlButtons[i].FontSize = FontSize[i];
                hlButtons[i].HorizontalContentAlignment = HorizontalAlignment.Center;
                Canvas.SetLeft(hlButtons[i], Left[i]);
                Canvas.SetTop(hlButtons[i], Top[i]);
                heart.Children.Add(hlButtons[i]);
                i++;

                //message.AppendFormat("Content:{0}, NavigateUri:{1}", Content, NavigateUri);
               // message.AppendLine();
               

            }
           

            //MessageBox.Show(message.ToString());

 
        }
    }
}
