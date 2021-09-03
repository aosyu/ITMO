; This file should be placed in clojure-solutions
; You may use it via (load-file "parser.clj")

(defn -return [value tail] {:value value :tail tail})
(def -valid? boolean)
(def -value :value)
(def -tail :tail)


(defn _empty [value] (partial -return value))

(defn _char [p]
  (fn [[c & cs]]
    (if (and c (p c)) (-return c cs))))

(defn _map [f]
  (fn [result]
    (if (-valid? result)
      (-return (f (-value result)) (-tail result)))))

(defn _combine [f a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar)
        ((_map (partial f (-value ar)))
         ((force b) (-tail ar)))))))

(defn _either [a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar) ar ((force b) str)))))

(defn _parser [p]
  (fn [input]
    (-value ((_combine (fn [v _] v) p (_char #{\u0001})) (str input \u0001)))))
(mapv (_parser (_combine str (_char #{\a \b}) (_char #{\x}))) ["ax" "ax~" "bx" "bx~" "" "a" "x" "xa"])



(defn +char [chars] (_char (set chars)))
(defn +char-not [chars] (_char (comp not (set chars))))
(defn +map [f parser] (comp (_map f) parser))
(def +ignore (partial +map (constantly 'ignore)))

(defn iconj [coll value]
  (if (= value 'ignore) coll (conj coll value)))

(defn +seq [& ps]
  (reduce (partial _combine iconj) (_empty []) ps))

(defn +seqf [f & ps] (+map (partial apply f) (apply +seq ps)))

(defn +seqn [n & ps] (apply +seqf (fn [& vs] (nth vs n)) ps))

(defn +or [p & ps]
  (reduce (partial _either) p ps))

(defn +opt [p]
  (+or p (_empty nil)))

(defn +star [p]
  (letfn [(rec [] (+or (+seqf cons p (delay (rec))) (_empty ())))] (rec)))

(defn +plus [p] (+seqf cons p (+star p)))

(defn +str [p] (+map (partial apply str) p))

(def +parser _parser)


(defn +collect [defs]
  (cond
    (empty? defs) ()
    (seq? (first defs)) (let [[[key args body] & tail] defs]
                          (cons
                            {:key key :args args :body body}
                            (+collect tail)))
    :else (let [[key body & tail] defs]
            (cons
              {:key key :args [] :synth true :body body}
              (+collect tail)))))

(defmacro defparser [name & rules]
  (let [collected (+collect rules)
        keys (set (map :key (filter :synth collected)))]
    (letfn [(rule [{key :key, args :args, body :body}] `(~key ~args ~(convert body)))
            (convert [value]
              (cond
                (seq? value) (map convert value)
                (char? value) `(+char ~(str value))
                (keys value) `(~value)
                :else value))]
      `(def ~name (letfn ~(mapv rule collected) (+parser (~(:key (last collected)))))))))



;; This file should be placed in clojure-solutions
;; You may use it via (load-file "parser.clj")
;
;; tabulate 10 min
;
;(defn -return [value tail] {:value value :tail tail})
;(def -valid? boolean)
;(def -value :value)
;(def -tail :tail)
;
;
;(defn _empty [value] (partial -return value))
;
;(defn _char [p]                                             ; p - предикат
;  (fn [[c & cs]]                                            ; получаем на вход строчку из первого символа и из остальных
;    (if (and c (p c)) (-return c cs))))                     ; если первый символ существует и удовл предикату
;
;(defn _map [f]                                              ; преобразовывает результат
;  (fn [result]
;    (if (-valid? result)
;      (-return (f (-value result)) (-tail result)))))
;
;(defn _combine [f a b]                                      ; a b парсеры, f применяем в конце к результатам парсинга
;  (fn [str]
;    (let [ar ((force a) str)]                               ; ar - результат парсера а по строчке str
;      (if (-valid? ar)
;        ((_map (partial f (-value ar)))
;         ((force b) (-tail ar)))))))
;
;(defn _either [a b]                                         ; принимает 2 парсера, пытается запустить первый, иначе запускает второй
;  (fn [str]
;    (let [ar ((force a) str)]
;      (if (-valid? ar) ar ((force b) str)))))
;
;(defn _parser [p]                                           ; парсит все целиком
;  (fn [input]
;    (-value ((_combine (fn [v _] v)                         ; f: принимает значение парсера p, _ так как результат втрого парсера не важен, возвращает v
;                       p
;                       (_char #{\u0001})
;                       )
;             (str input \u0001)))))                         ; (str input \u0001) добавляем в конец инпута символ
;
;(mapv (_parser (_combine str (_char #{\a \b}) (_char #{\x}))) ["ax" "ax~" "bx" "bx~" "" "a" "x" "xa"])
;
;
;; + - более удобные версии
;(defn +char [chars] (_char (set chars)))                    ; (+char "abc") <=> (_char #{\a \b \c})
;(defn +char-not [chars] (_char (comp not (set chars))))     ; парсит все символы кроме перечесленных chars
;(defn +map [f parser] (comp (_map f) parser))               ; берет то, что выдал парсер, и применяет к нему функцию
;(def +ignore (partial +map (constantly 'ignore)))           ; меняет результат парсера на игнор
;
;(defn iconj [coll value]                                    ; добавляем к списку call любой элемент, кроме 'ignore
;  (if (= value 'ignore) coll (conj coll value)))            ; (iconj '(1 2 3) 234) => (234 1 2 3)
;
;; возвращает вектор, в котором хранятся результаты применения каждого парсера по очереди
;(defn +seq [& ps]                                           ; последовательность из произвольного поличества парсеров
;  (reduce (partial _combine iconj) (_empty []) ps))         ; (_empty []) возвращает ничего
;
;; (mapv (_parser (_combine str (_char #{\a \b}) (_char #{\x}))) ["ax" "ax~" "bx" "bx~" "" "a" "x" "xa"])
;; эквивалент (mapv (+seq (+char "ab") (+char "x")) ["ax" "ax~" "bx" "bx~" "" "a" "x" "xa"])
;
;
;
;(defn +seqf [f & ps] (+map (partial apply f) (apply +seq ps))) ; применяет функцию ко всем элементам вектора
;
;(defn +seqn [n & ps] (apply +seqf (fn [& vs] (nth vs n)) ps)) ; принимает набор значений и выделяет n-тое
;; удобно, когда нужно выделить что-то, обрамленное какими-то значениями, например, (x)
;(defn +or [p & ps]                                          ; 45 минута. возвращается значение первого парсера, которому удалось распарсить выражение
;  (reduce (partial _either) p ps))
;
;(defn +opt [p]                                              ; если не получилось распарсить, возвращаем nil и все что было подано на вход
;  (+or p (_empty nil)))
;
;(defn +star [p]                                             ; парсит столько раз, сколько возможно
;  (letfn [(rec [] (+or (+seqf cons p (delay (rec))) (_empty ())))] (rec)))
;
;(defn +plus [p] (+seqf cons p (+star p)))                   ; как звездочка, только требует наличие хотя бы одного символа, иначе не парсит
;; а звездочка вернет () | str
;(defn +str [p] (+map (partial apply str) p))                ;
;
;(def +parser _parser)
;
;
;(def *digit (+char "0123456789"))
;(def *number (+map read-string (+str (+plus *digit))))
;(def *string (+seqn 1 (+char "\"") (+str (+star (+char-not "\""))) (+char "\"")))
;(def *space (+char " \t\r\n"))
;(def *ws (+ignore (+star *space)))                          ; skipWs/ignoreWs
;(def *null (+seq (constantly 'null) (+char "n") (+char "u") (+char "l") (+char "l")))
;(def *letter (+char (apply str (filter #(Character/isLetter %) (mapv char (range 32 128))))))
;(def *identifier (+str (+seqf cons *letter (+star (+or *letter *digit))))) ; парсит слово (из букв и цифр)                                        ; начинается с буквы или подчеркивания
;(def +string (+str (+seqf cons *letter (+star *letter))))
;(defn *seq [begin p end]
;  (+seqn 1 (+char begin) (+opt (+seqf *ws cons p (+star (+seqn 1 *ws (+char ",") *ws p)))) *ws (+char end)))
;(defn *array [p] (*seq "[" p "]"))
;(defn *member [p] (+seq *identifier *ws (+ignore (+char ":")) *ws p)) ; объект вида "a: 2", "a : 2" и тд
;(defn *object [p]
;  (+map (partial reduce #(apply assoc %1 %2) {})
;        ;(+map (fn [values] (reduce (fn [m [k v]] (assoc m k v)) {} values))
;        (*seq "{" (*member p) "}")))
;(def *json
;  (letfn [(*value []                                        ; функция, которая принимает 0 аргументов
;            (delay (+or
;                     *null
;                     *number
;                     *string
;                     (*object (*value))
;                     (*array (*value)))))]
;    (_parser (+seqn 0 *ws (*value)))))                      ; 1:38
;
;; 1:40 - неоднозначные парсеры
;; 1:46 - оптимизация
;
;;(defmacro infix [[a op b]] (list op a b))                   ; (infix (10 + 20)) => 30
;; или (defmacro infix [[a op b]] обратная ковычка(волнаop волнаа волнаb))
;(defmacro infix [expr]
;  (letfn [(convert [e]
;            (if (list? e)
;              (let [[a op b] e] `(~op ~(convert a) ~(convert b)))
;              e))]
;    (convert expr)))                                        ; (infix 10 + (20 + 30)) => 60
;
;;(defn +string [s] (+str (apply +seq (map (comp +char str) (char-array s)))))
;
;(defn +collect [defs]
;  (cond
;    (empty? defs) ()
;    (seq? (first defs)) (let [[[key args body] & tail] defs]
;                          (cons
;                            {:key key :args args :body body}
;                            (+collect tail)))
;    :else (let [[key body & tail] defs]
;            (cons
;              {:key key :args [] :synth true :body body}
;              (+collect tail)))))
;
;(defmacro defparser [name & rules]
;  (let [collected (+collect rules)
;        keys (set (map :key (filter :synth collected)))]
;    (letfn [(rule [{key :key, args :args, body :body}] `(~key ~args ~(convert body)))
;            (convert [value]
;              (cond
;                (seq? value) (map convert value)
;                (char? value) `(+char ~(str value))
;                (keys value) `(~value)
;                :else value))]
;      `(def ~name (letfn ~(mapv rule collected) (+parser (~(:key (last collected)))))))))
;
;; 2 - 3 * 6 / x