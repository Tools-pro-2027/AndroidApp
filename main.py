from kivy.app import App
from kivy.uix.screenmanager import ScreenManager, Screen
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.label import Label
from kivy.uix.button import Button
from kivy.graphics import Color, Rectangle
import socket
import threading

class MenuScreen(Screen):
    def __init__(self, **kwargs):
        super(MenuScreen, self).__init__(**kwargs)
        with self.canvas.before:
            Color(0.05, 0.05, 0.08, 1)
            self.rect = Rectangle(size=self.size, pos=self.pos)
        self.bind(size=self._update_rect, pos=self._update_rect)
        layout = BoxLayout(orientation='vertical', padding=50, spacing=20)
        title = Label(text='[b]DREAM HORROR: THE AWAKENING[/b]', markup=True, font_size=32, color=(0.8, 0.1, 0.1, 1))
        story_label = Label(text='أنت محبوس في كابوس لا ينتهي...\nاكتشف الحصن، ابحث عن المخرج، ولا تنظر خلفك.', font_size=16, color=(0.7, 0.7, 0.7, 1), halign='center')
        btn_start = Button(text='بدء القصة السينمائية', background_color=(0.6, 0.1, 0.1, 1), font_size=18)
        btn_start.bind(on_press=self.start_game)
        btn_multi = Button(text='لعب مع صديق (نقطة اتصال)', background_color=(0.2, 0.2, 0.3, 1), font_size=18)
        btn_multi.bind(on_press=self.goto_multiplayer)
        layout.add_widget(title)
        layout.add_widget(story_label)
        layout.add_widget(btn_start)
        layout.add_widget(btn_multi)
        self.add_widget(layout)
    def _update_rect(self, instance, value):
        self.rect.pos = instance.pos
        self.rect.size = instance.size
    def start_game(self, instance):
        self.manager.current = 'game'
    def goto_multiplayer(self, instance):
        self.manager.current = 'multi'

class GameScreen(Screen):
    def __init__(self, **kwargs):
        super(GameScreen, self).__init__(**kwargs)
        layout = BoxLayout(orientation='vertical', padding=30)
        self.info = Label(text='[جاري تحميل التفاصيل السينمائية والجرافيك العالي...]\nالمنطقة مظلمة ومخيفة...', markup=True, font_size=20, color=(0.9, 0.9, 0.9, 1))
        btn_back = Button(text='العودة للقائمة', size_hint=(1, 0.2), background_color=(0.3, 0.3, 0.3, 1))
        btn_back.bind(on_press=self.back_to_menu)
        layout.add_widget(self.info)
        layout.add_widget(btn_back)
        self.add_widget(layout)
    def back_to_menu(self, instance):
        self.manager.current = 'menu'

class MultiplayerScreen(Screen):
    def __init__(self, **kwargs):
        super(MultiplayerScreen, self).__init__(**kwargs)
        layout = BoxLayout(orientation='vertical', padding=40, spacing=15)
        title = Label(text='العب مع صديق عبر نقطة الاتصال (Hotspot)', font_size=20, color=(1, 0.8, 0, 1))
        self.status_label = Label(text='حالة الاتصال: غير متصل', font_size=16)
        btn_host = Label(text='قم بإنشاء نقطة اتصال من هاتفك واطلب من صديقك الاتصال بها', font_size=14, color=(0.8, 0.8, 0.8, 1))
        btn_connect = Button(text='الاتصال بالخادم المحلي (صديقك)', background_color=(0.1, 0.5, 0.2, 1))
        btn_connect.bind(on_press=self.start_lan_connection)
        btn_back = Button(text='رجوع', size_hint=(1, 0.2), background_color=(0.3, 0.3, 0.3, 1))
        btn_back.bind(on_press=self.back_to_menu)
        layout.add_widget(title)
        layout.add_widget(self.status_label)
        layout.add_widget(btn_host)
        layout.add_widget(btn_connect)
        layout.add_widget(btn_back)
        self.add_widget(layout)
    def start_lan_connection(self, instance):
        self.status_label.text = "جاري البحث عن الصديق عبر الشبكة المحلية..."
        threading.Thread(target=self.connect_socket, daemon=True).start()
    def connect_socket(self):
        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.settimeout(3)
            s.connect(('192.168.43.1', 5000))
            self.status_label.text = "تم الاتصال بالصديق بنجاح!"
            s.close()
        except:
            self.status_label.text = "فشل الاتصال تأكد أن صديقك فتح الخادم ونقطة الاتصال نشطة."
    def back_to_menu(self, instance):
        self.manager.current = 'menu'

class DreamHorrorApp(App):
    def build(self):
        sm = ScreenManager()
        sm.add_widget(MenuScreen(name='menu'))
        sm.add_widget(GameScreen(name='game'))
        sm.add_widget(MultiplayerScreen(name='multi'))
        return sm

if __name__ == '__main__':
    DreamHorrorApp().run()
