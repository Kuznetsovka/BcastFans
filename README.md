# Программа массового подбора вентиляторов

## Назначение приложения

Снижение временных затрат на получение технических листов согласно ХОВС

## Используемые технологии

* JavaFX
* Java
* Maven
* Библиотеки для работы с [Excel Apache Poi](https://poi.apache.org/index.html)
* Selenium для парсинга сайта


## Интересные особенности программы
* Многопоточность для работы прогресс бара процесса выполнения
* Хэширование уже подобранных систем для экономии времени подбора.

## Установка
Копирем папку DriverChrome в директорию: C:\ProgramData\, должен получится путь: C:\ProgramData\DriverChrome\...[содержимое папки]
Переместите ярлык в удобное для использования место.

## Использование
1. Заполнение данных в Broadcast fans ОЛ.xls 
   1. Возможные типы вентиляторов для расчета:
         - Тип монтажа:  Круглый, Прямоугольный, Круг & Прямог., Крышный
         - Тип установки: Кухонный, Шумоизолированный, EC, На крыше, Дымоудаление, Шумоизолированный & EC, Кухонный & EC. 
   
      Комментарии:
         Круг & Прямог. - выбор будет производиться из обоих типов.
         На крыше - выбирается K или MUB серия.
         Дымоудаление - выбирает вентиляторы на 400°C.
2. После загрузки программы нажимаем кнопку Load. В выпадающем списке выбираем заполненный опросный лист.
   Загрузка длится долго, идет подготовка браузера к работе, открытие сайта systemair.com.
3. Перед расчетом есть доп. функции:
   1. Выбор пути сохранения файла технических листов и excel файла подобранных систем.
      - Нажимаем галочку путь сохранения
      - Кликаем на поле
      - Выбираем желаемую папку в выпавшем диалоговом окне 
   2. Выбор отрицательного и положительного допуска. 
   3. Выбор режима расчета с выгрузкой расчетных листов или без, галочка Да/Нет. 
   4. Открыв вкладну Вторичное можно выбрать дотустимые типоразмеры вентиляторов, для каждого из разделов: Круглые, Прямоугольные, Крышные.
         Возможен Мультивыбор, т.е. зажимая Ctrl + левый клик добавляется выбранный тип, Зажимая Shift + левый клик можно выбрать несколько
         моделей пока мышь не отжата. 
   5. Выбор какую систему считать, а какую нет. 
   6. Выбор получения результата
         - Заполнение всех подобранных вентиляторов после расчета. (немного ускоряет процесс подбора)
         - Заполнение подобранного вентилятора по одному. 
   7. Есть возможность скорректировать входные данные: номер системы, Расход, Потери, Тип Монтажа и Тип установки.
         Выбрав нужное поле и изменив его новое знанение сохраниться после нажатия Enter. 
   8. Кнопка Clear очищаем таблицу.
4. Нажмите кнопку Calculate. Если выбран режим заполнение по 1 вентилятору, тогда заполнение будет постепенным. 
   1. В любой момент Вы можете нажать кнопку СТОП. Работает не мгновенно, в момент перехода на расчет новго вентрилятора. 
   2. Выбирается самый дешовый вентиялтор сответствующий входным параметрам.
5. Нажмите Save as для выгрузки excel файла результата. Укажите путь сохранения и название файла в диалоговом окне.

РЕКОМЕНДАЦИИ: Выбирать режим "Заполнение подобранного вентилятора по одному".
Тем самым если что-то пойдет не так, например компьютер потерят интернет соединение, у вас останется промежуточный результат,
который вы сможете сохранить и продолжить расчет с того места где остановились.

ЗАМЕЧАНИЕ:
Если версия Вашего брайзера выше 93, тогда потребуется заменить содержимое папки: \chromedriver_win32_93, пишите помогу.



