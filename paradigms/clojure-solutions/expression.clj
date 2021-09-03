(defn operation [f]
  (fn [& operands] (fn [vars]
                     (apply f (mapv (fn [operand] (operand vars)) operands)))))

(def negate (operation -))
(def multiply (operation *))
(def add (operation +))
(def subtract (operation -))
(def avg (operation (fn [& operands] (/ (apply + operands) (count operands)))))
(def sum add)
(def divide (operation (fn ([x] (/ 1.0 x))
                         ([first & rest] (/ (double first) (apply * rest))))))


(def constant constantly)
(defn variable [variable] (fn [values] (get values variable)))
(def operations {
                 'negate negate
                 '*      multiply
                 '+      add
                 '-      subtract
                 '/      divide
                 'sum    sum
                 'avg    avg
                 }
  )

(defn parseExpression [expression]
  (cond
    (number? expression) (constant expression)
    (seq? expression) (apply (operations (first expression)) (map parseExpression (rest expression)))
    (symbol? expression) (variable (str expression))))

(defn parseFunction
  [expression]
  (parseExpression (read-string expression)))

;----------------------------------------------------object-------------------------------------------------------------
(load-file "proto.clj")

(def evaluate (method :evaluate))
(def toString (method :toString))
(def diff (method :diff))
(def toStringInfix (method :toStringInfix))

(def Constant)
(def ConstantPrototype
  (let [value (field :value)]
    {
     :evaluate      (fn [this & _] (value this))
     :toString      (fn [this] (str (format "%.1f" (float (value this)))))
     :diff          (fn [_ _] (Constant 0))
     :toStringInfix (fn [this] (toString this))
     }))

(defn ConstantConstructor [this value]
  (assoc this
    :value value))

(def Constant (constructor ConstantConstructor ConstantPrototype))

(def ZERO (Constant 0))
(def ONE (Constant 1))

(def VariablePrototype
  (let [name (field :name)]
    {
     :evaluate      (fn [this values] (get values (clojure.string/lower-case (str (first (name this))))))
     :toString      (fn [this] (name this))
     :diff          (fn [this var]
                      (if (= (name this) var) ONE ZERO))
     :toStringInfix (fn [this] (toString this))}))

(defn VariableConstructor [this name]
  (assoc this
    :name name))

(def Variable (constructor VariableConstructor VariablePrototype))

(def OperationPrototype
  (let [operands (field :operands)
        sign (field :sign)
        function (field :function)
        diffCalc (field :diffCalc)]
    {
     :evaluate      (fn [this vars]
                      (apply (function this)
                             (mapv (fn [operand] (evaluate operand vars))
                                   (operands this))))
     :toString      (fn [this]
                      (str "(" (sign this) " "
                           (clojure.string/join " " (mapv toString (operands this)))
                           ")"))
     :diff          (fn [this variable]
                      ((diffCalc this) (operands this) (mapv (fn [operand] (diff operand variable)) (operands this))))
     :toStringInfix (fn [this]
                      (let [infix (mapv toStringInfix (operands this))]
                        (if (== (count infix) 1)
                          (str (sign this) "(" (first infix) ")")
                          (str "(" (first infix) " " (sign this) " " (last infix) ")")
                          )
                        )
                      )
     }))

(defn OperationConstructor
  [sign function diffCalc]
  (fn [& operands]
    {:prototype {:prototype OperationPrototype
                 :sign      sign
                 :function  function
                 :diffCalc  diffCalc
                 }
     :operands  (vec operands)}))

(def Negate
  (OperationConstructor "negate" - (fn [_ diffedArgs] (apply Negate diffedArgs))))

(def Add
  (OperationConstructor "+" + (fn [_ diffedArgs] (apply Add diffedArgs))))

(def Subtract
  (OperationConstructor "-" - (fn [_ diffedArgs] (apply Subtract diffedArgs))))

(def Multiply)
(def multiplyDiffCalc (fn [args diffedArgs]
                        (second (reduce (fn [[f df] [g dg]]
                                          [(Multiply f g)
                                           (Add (Multiply f dg)
                                                (Multiply df g))])
                                        [ONE ZERO]
                                        (mapv vector args diffedArgs)))
                        ))

(def Multiply
  (OperationConstructor "*" * multiplyDiffCalc))

(def Divide
  (OperationConstructor "/" (fn ([x] (/ 1 (double x)))
                              ([first & rest] (/ first (double (apply * rest)))))
                        (fn [[first & rest] [diffFirst & diffRest]]
                          (if (empty? rest)
                            (Multiply
                              diffFirst
                              (Negate
                                (Divide
                                  ONE
                                  (Multiply first first))))

                            (let [mulRest (apply Multiply rest)]
                              (Divide
                                (Subtract
                                  (Multiply diffFirst mulRest)
                                  (Multiply (multiplyDiffCalc rest diffRest) first))
                                (Multiply mulRest mulRest))
                              )
                            )
                          )
                        ))

(def Sum (OperationConstructor "sum" +
                               (fn [_ diffedArgs] (apply Sum diffedArgs))))

(def Avg (OperationConstructor "avg"
                               (fn [& args] (/ (reduce + args) (count args)))
                               (fn [args diffedArgs] (Divide (apply Add diffedArgs)
                                                             (Constant (count args))))))

(def IPow (OperationConstructor "**" (fn [& args] (let [reversed (reverse args)]
                                                    (reduce (fn [x y] (Math/pow y x)) reversed)))
                                (constantly nil)))

(def ILog (OperationConstructor "//" (fn [& args] (let [reversed (reverse args)]
                                                    (reduce (fn [x y] (/ (Math/log (Math/abs x)) (Math/log (Math/abs y)))) reversed)))
                                (constantly nil)))

(def objectOperations
  {
   "+"      Add
   "-"      Subtract
   "*"      Multiply
   "/"      Divide
   "negate" Negate
   "sum"    Sum
   "avg"    Avg
   "**"     IPow
   "//"     ILog
   })

(defn parseObjectExpr [expr]
  (cond
    (number? expr) (Constant expr)
    (seq? expr) (apply (objectOperations (str (first expr))) (mapv parseObjectExpr (rest expr)))
    :else (Variable (str expr))))

(defn parseObject
  [expression]
  (parseObjectExpr (read-string expression)))


;----------------------------------------------------parser-------------------------------------------------------------
(load-file "parser.clj")
(def *digit (+char "0123456789"))
(def *space (+char " \t\r\n"))
(def *ws (+ignore (+star *space)))

(defn _show [result]
  (if (-valid? result)
    (str "-> " (pr-str (-value result)) " | " (pr-str (apply str (-tail result)))) "!"))
(defn tabulate [parser inputs]
  (run! (fn [input] (printf "    %-10s %s\n" input (_show (parser input)))) inputs))

(defn -showObj [result]
  (if (-valid? result) (str "-> " (toStringInfix result) " | " (pr-str (apply str (-tail result)))) "!"))
(defn tabulateObj [parser inputs]
  (run! (fn [input] (printf "    %-10s %s\n" (pr-str input) (-showObj (parser input)))) inputs))

;-----------------------------------------------------------------------------------------------------------------------
(declare add-sub)
(def *word (fn [chars] (+str (+plus (+char chars)))))
(def *variable (+map Variable (*word "xXyYzZ")))
(def *const (+map (comp Constant read-string)
                  (+str (+seqf concat
                               (+seqf cons (+opt (+char "-")) (+plus *digit))
                               (+seqf cons (+opt (+char ".")) (+star *digit))))))

(defn get-result
  [left-sided res]
  (let [args (if left-sided res (reverse res))]
    (reduce (fn [x y] (apply (get objectOperations (first y)) (if left-sided [x (second y)] [(second y) x])))
            (first args) (partition 2 (rest args)))))

(defn +operation [signs]
  (+map str (apply +or (map (fn [s] (+str (apply +seq (map (comp +char str) (char-array s))))) signs))))

; беру массив ["+" "-"], спличу каждую операцию по симполам, парсю каждый символ, конвертирую это все к строке

(defn abstract [next sign left-sided]
                (+map (partial get-result left-sided)
                      (+seqf cons *ws next *ws
                             (+map (partial apply concat)
                                   (+star (+seq (+operation sign) *ws next *ws))
                                   ))))

(def unary (delay (+or *variable
                       (+seqn 1 (+char "(") *ws add-sub *ws (+char ")"))
                       (+map Negate (+seqn 1 (*word "negate") *ws unary))
                       *const)))

(def pow-log (abstract unary ["**" "//"] false))
(def mul-div (abstract pow-log ["*" "/"] true))
(def add-sub (abstract mul-div ["+" "-"] true))

(def parser (+parser add-sub))
(defn parseObjectInfix [exp]
  (parser exp))
