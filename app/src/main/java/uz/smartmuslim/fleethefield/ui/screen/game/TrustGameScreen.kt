package uz.smartmuslim.fleethefield.ui.screen.game

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.smartmuslim.fleethefield.R
import uz.smartmuslim.fleethefield.databinding.GameOverDialogBinding
import uz.smartmuslim.fleethefield.databinding.TrustGameScreenBinding
import uz.smartmuslim.fleethefield.databinding.WinDialogBinding
import uz.smartmuslim.fleethefield.model.*
import uz.smartmuslim.fleethefield.utils.MySharedPreferences
import uz.smartmuslim.fleethefield.utils.dp
import java.lang.Math.abs


class TrustGameScreen : Fragment() {

    private lateinit var binding: TrustGameScreenBinding
    private var listConst = arrayOfNulls<Item>(63)
    private val random = java.util.Random()
    private val mediaPlayer: MediaPlayer? = null
    private lateinit var shuffleList: ArrayList<Item>
    private var adapter = ItemAdapter()
    lateinit var sharedPreferences: MySharedPreferences
    var levelCount = 1
    var oldX = 0
    var oldY = 0
    private var score = 0
    var bonusOne = false
    var bonusTwo = false
    var bonusThree = false
    var bonusCount = 0
    private var personPosition = 59
    private var botPosition = 24

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = TrustGameScreenBinding.inflate(layoutInflater, container, false)

        MySharedPreferences.init(requireContext())
        sharedPreferences = MySharedPreferences

        binding.score.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Your level :" + sharedPreferences.levelCount.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }

        if (sharedPreferences.sound == true) {
            SoundGame.create(requireContext())
            SoundMenu.mediaPlayer.stop()
        } else {
            SoundGame.mediaPlayer.stop()
        }
        levelCount = sharedPreferences.levelCount!!
        bonusCount = sharedPreferences.bonusCount!!
        initListConst()
        shuffleList()
        bonusMethod()
        setAdapter()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setAdapter() {
        var n = 0
        listConst.forEachIndexed { index, item ->
            if (item == null) {
                listConst[index] = shuffleList[n]
                n++
            }
        }
        adapter.setData(listConst.toList().requireNoNulls())
        binding.rv.adapter = adapter

        binding.rv.setOnTouchListener { view, motionEvent ->

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    oldX = motionEvent.x.toInt()
                    oldY = motionEvent.y.toInt()
                }
                MotionEvent.ACTION_UP -> {
                    val newX = motionEvent.x
                    val newY = motionEvent.y
                    val distanceX = newX - oldX
                    val distanceY = newY - oldY
                    if (kotlin.math.abs(distanceX) > kotlin.math.abs(distanceY) && kotlin.math.abs(
                            distanceX.dp
                        ) > 5.dp
                    ) {
                        if (distanceX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                    } else if (abs(distanceY).dp > 5.dp) {
                        if (distanceY < 0) {
                            onSwipeTop()
                        } else {

                            onSwipeBottom()
                        }
                    }
                }
            }
            true
        }
    }

    private fun onSwipeBottom() {

        val bottomPos = personPosition + 7
        score++
        if (listConst.size > bottomPos)
            when (listConst[bottomPos]?.type) {
                Type.Empty -> {
                    val item = listConst[personPosition]
                    listConst[personPosition] = listConst[bottomPos]
                    listConst[bottomPos] = item
                    personPosition = bottomPos
                }
                Type.Stone -> {
                    Toast.makeText(requireContext(), "Stone", Toast.LENGTH_SHORT).show()
                }
                Type.None -> {
                    Toast.makeText(requireContext(), "Wall", Toast.LENGTH_SHORT).show()
                }
                Type.Flag -> {
                    listConst[bottomPos] = listConst[personPosition]
                    listConst[personPosition] =
                        Item(null, R.drawable.back_style_rec, null, Type.Empty)
                    personPosition = bottomPos
                    score -= 5
                }
                Type.Bot -> {
                    gameOverDialog()
                    if (sharedPreferences.vibration == true && sharedPreferences.gameOver == true) {
                        vibrate()
                        SoundGameOver?.mediaPlayer.start()
                    }
                }
                else -> {}
            }
        botAction()
        binding.score.text = score.toString()
        adapter.setData(listConst.toList().requireNoNulls())

    }

    private fun onSwipeTop() {

        score++
        var topPos = personPosition - 7
        if (listConst.size > topPos && topPos >= 0)
            when (listConst[topPos]?.type) {
                Type.Empty -> {

                    bonusTopAction(topPos)
                    val item = listConst[personPosition]
                    listConst[personPosition] = listConst[topPos]
                    listConst[topPos] = item
                    personPosition = topPos
                }
                Type.Stone -> {
                    Toast.makeText(requireContext(), "Stone", Toast.LENGTH_SHORT).show()
                }
                Type.None -> {
                    Toast.makeText(requireContext(), "Wall", Toast.LENGTH_SHORT).show()
                }
                Type.Flag -> {
                    listConst[topPos] = listConst[personPosition]
                    listConst[personPosition] =
                        Item(null, R.drawable.back_style_rec, null, Type.Empty)
                    personPosition = topPos
                    score -= 5
                }
                Type.Bot -> {
                    gameOverDialog()
                    if (sharedPreferences.vibration == true && sharedPreferences.gameOver == true) {
                        vibrate()
                        SoundGameOver?.mediaPlayer.start()
                    }
                }
                Type.Exit -> {

                    listConst[topPos] = listConst[personPosition]
                    listConst[personPosition] =
                        Item(null, R.drawable.back_style_rec, null, Type.Empty)
                    personPosition = topPos
                    winDialog()
                }
                else -> {}
            }
        botAction()
        binding.score.text = score.toString()
        adapter.setData(listConst.toList().requireNoNulls())

    }

    private fun bonusTopAction(topPos: Int) {
        var topPosFunc = topPos
        if (personPosition >= 14)

            when {
                bonusOne -> {
                    when (listConst[topPosFunc]?.type) {

                        Type.Empty -> {
                            topPosFunc -= 7
                            bonusOne = false
                        }
                        Type.None -> {
                            //  bonusLeftAction(topPosFunc)
                            bonusOne = false
                        }
                        Type.Exit -> {
                            winDialog()
                            bonusOne = false
                        }
                        Type.Bot -> {
                            gameOverDialog()
                            bonusOne = false
                        }
                        Type.Flag -> {
                            listConst[topPosFunc] = listConst[personPosition]
                            listConst[personPosition] =
                                Item(null, R.drawable.back_style_rec, null, Type.Empty)
                            personPosition = topPosFunc + 1
                            score -= 5

                        }
                        else -> {}
                    }

                }
                bonusTwo -> {
                    topPosFunc -= 7
                    bonusTwo = false
                }
                bonusThree -> {
                    topPosFunc -= 7
                    bonusThree = false
                }
            }
    }

    private fun onSwipeLeft() {

        score++
        var leftPos = personPosition - 1
        when (listConst[leftPos]?.type) {
            Type.Empty -> {

                if (personPosition in 16..20 && personPosition in 23..27 && personPosition in 30..34 &&
                    personPosition in 37..41 && personPosition in 44..48 && personPosition in 9..13
                    && personPosition in 52..54
                )
                    when {
                        bonusOne -> {
                            leftPos -= 2
                            bonusOne = false
                        }
                        bonusTwo -> {
                            leftPos -= 2
                            bonusTwo = false
                        }
                        bonusThree -> {
                            leftPos -= 2
                            bonusThree = false
                        }
                    }
                val item = listConst[personPosition]
                listConst[personPosition] = listConst[leftPos]
                listConst[leftPos] = item
                personPosition = leftPos
            }
            Type.Stone -> {
                Toast.makeText(requireContext(), "Stone", Toast.LENGTH_SHORT).show()
            }
            Type.None -> {
                Toast.makeText(requireContext(), "Wall", Toast.LENGTH_SHORT).show()
            }
            Type.Flag -> {
                listConst[leftPos] = listConst[personPosition]
                listConst[personPosition] =
                    Item(null, R.drawable.back_style_rec, null, Type.Empty)
                personPosition = leftPos
                score -= 5
            }
            Type.Bot -> {
                gameOverDialog()
                if (sharedPreferences.vibration == true && sharedPreferences.gameOver == true) {
                    vibrate()
                    SoundGameOver?.mediaPlayer.start()
                }
            }
            Type.Exit -> {
                listConst[leftPos] = listConst[personPosition]
                listConst[personPosition] =
                    Item(null, R.drawable.back_style_rec, null, Type.Empty)
                personPosition = leftPos
                winDialog()
            }
            else -> {}
        }
        botAction()
        binding.score.text = score.toString()
        adapter.setData(listConst.toList().requireNoNulls())
    }

    private fun onSwipeRight() {
        score++
        var rightPos = personPosition + 1
        when (listConst[rightPos]?.type) {
            Type.Empty -> {

                if (personPosition in 14..18 && personPosition in 21..25 && personPosition in 28..32 &&
                    personPosition in 35..39 && personPosition in 42..46 && personPosition in 50..52
                    && personPosition in 8..10
                ) {
                    when {
                        bonusOne -> {
                            rightPos += 2
                            bonusOne = false
                        }
                        bonusTwo -> {
                            rightPos += 2
                            bonusTwo = false
                        }
                        bonusThree -> {
                            rightPos += 2
                            bonusThree = false
                        }
                    }
                }
                listConst[rightPos] = listConst[personPosition]
                listConst[personPosition] =
                    Item(null, R.drawable.back_style_rec, null, Type.Empty)
                personPosition = rightPos
            }
            Type.Stone -> {
                Toast.makeText(requireContext(), "Stone", Toast.LENGTH_SHORT).show()
            }
            Type.None -> {
                Toast.makeText(requireContext(), "Wall", Toast.LENGTH_SHORT).show()
            }
            Type.Flag -> {
                listConst[rightPos] = listConst[personPosition]
                listConst[personPosition] =
                    Item(null, R.drawable.back_style_rec, null, Type.Empty)
                personPosition = rightPos
                score -= 5
            }
            Type.Bot -> {
                gameOverDialog()

                if (sharedPreferences.vibration == true && sharedPreferences.gameOver == true) {
                    vibrate()
                    SoundGameOver?.mediaPlayer.start()
                }
            }
            Type.Exit -> {
                listConst[rightPos] = listConst[personPosition]
                listConst[personPosition] =
                    Item(null, R.drawable.back_style_rec, null, Type.Empty)
                personPosition = rightPos
                winDialog()
            }
            else -> {}
        }
        botAction()
        binding.score.text = score.toString()
        adapter.setData(listConst.toList().requireNoNulls())
    }

    private fun botAction() {

        when (personPosition) {
            in 0..20 -> {
                val list = listOf(-7, -1, 1)
                when (list.random()) {
                    -7 -> {
                        if (botPosition >= 8) {
                            botTopAction()
                        } else {
                            botAction()
                        }

                    }
                    -1 -> {
                        botLeftAction()
                    }
                    1 -> {
                        botRightAction()
                    }
                }
            }
            in 20..62 -> {
                val list = listOf<Int>(-1, 1)
                when (list.random()) {
                    -1 -> {
                        botLeftAction()
                    }
                    1 -> {
                        botRightAction()
                    }
                }
            }
        }
    }

    private fun botLeftAction() {

        val newPosition = botPosition - 1

        when (listConst[newPosition]?.type) {
            Type.Empty -> {

                if (botPosition == 21 || botPosition == 14 || botPosition == 8) {
                    botRightAction()
                } else if (personPosition == newPosition) {
                    gameOverDialog()
                } else {
                    val item = listConst[botPosition]
                    listConst[botPosition] = listConst[newPosition]
                    listConst[newPosition] = item
                    botPosition = newPosition
                }
            }
            Type.Stone -> {
//                botRightAction()
                botAction()
            }
            Type.None -> {
                //                botRightAction()
                botAction()
            }
            Type.Flag -> {
                //                botRightAction()
                botAction()
            }
            Type.Exit -> {
                botBottomAction()
            }
            Type.Person -> {
                if (botPosition == 21 || botPosition == 14 || botPosition == 8) {
                    botRightAction()
                } else if (newPosition == personPosition) {
                    gameOverDialog()
                }
            }

            else -> {}
        }
        adapter.setData(listConst.toList().requireNoNulls())
    }

    private fun botBottomAction() {
        val newPosition = botPosition + 7

        when (listConst[newPosition]?.type) {
            Type.Empty -> {
                if (personPosition == newPosition) {
                    gameOverDialog()
                } else {
                    val item = listConst[botPosition]
                    listConst[botPosition] = listConst[newPosition]
                    listConst[newPosition] = item
                    botPosition = newPosition
                }
            }
            Type.Stone -> {
                //                botLeftAction()
                botAction()
            }
            Type.None -> {
                //                botLeftAction()
                botAction()
            }
            Type.Flag -> {
                //                botLeftAction()
                botAction()
            }
            Type.Exit -> {
                botBottomAction()
            }
            Type.Person -> {

                if (newPosition == personPosition) {
                    gameOverDialog()
                }
                else {
                    botAction()
                }
            }

            else -> {}
        }
        adapter.setData(listConst.toList().requireNoNulls())
    }

    private fun botRightAction() {

        val newPosition = botPosition + 1

        when (listConst[newPosition]?.type) {
            Type.Empty -> {

                if (botPosition == 12 || botPosition == 20 || botPosition == 27) {
                    //                botLeftAction()
                    botAction()
                } else if (personPosition == newPosition) {
                    gameOverDialog()
                } else {
                    val item = listConst[botPosition]
                    listConst[botPosition] = listConst[newPosition]
                    listConst[newPosition] = item
                    botPosition = newPosition
                }
            }
            Type.Stone -> {
                //                botLeftAction()
                botAction()
            }
            Type.None -> {
                //                botLeftAction()
                botAction()
            }
            Type.Flag -> {
                //                botLeftAction()
                botAction()
            }
            Type.Exit -> {
                botBottomAction()
            }
            Type.Person -> {

                if (botPosition == 12 || botPosition == 20 || botPosition == 27) {
                    botLeftAction()
                } else if(newPosition == personPosition) {
                    gameOverDialog()
                }
            }

            else -> {}
        }
        adapter.setData(listConst.toList().requireNoNulls())
    }

    private fun botTopAction() {

        val newPosition = botPosition - 7

        when (listConst[newPosition]?.type) {
            Type.Empty -> {
                if (personPosition == newPosition) {
                    gameOverDialog()
                } else {
                    val item = listConst[botPosition]
                    listConst[botPosition] = listConst[newPosition]
                    listConst[newPosition] = item
                    botPosition = newPosition
                }
            }
            Type.Stone -> {
                //                botRightAction()
                botAction()
            }
            Type.None -> {
                //                botRightAction()
                botAction()
            }
            Type.Flag -> {
                //                botRightAction()
                botAction()
            }
            Type.Exit -> {
                botBottomAction()
            }
            Type.Person -> {
                if (botPosition == 12 || botPosition == 20 || botPosition == 27) {
                    botLeftAction()
                } else {
                    botAction()
                }
            }

            else -> {}
        }
        adapter.setData(listConst.toList().requireNoNulls())
    }


    fun initListConst() {

        listConst[0] = Item(null, R.drawable.box_one_bg, null, Type.None)
        listConst[1] = Item(null, R.drawable.box_one_bg, null, Type.None)
        listConst[3] = Item(null, R.drawable.ic_union__3_, null, Type.Exit)
        listConst[5] = Item(null, R.drawable.box_one_bg, null, Type.None)
        listConst[6] = Item(null, R.drawable.box_one_bg, null, Type.None)
        listConst[7] = Item(null, R.drawable.box_one_bg, null, Type.None)
        listConst[13] = Item(null, R.drawable.box_one_bg, null, Type.None)
        listConst[24] = Item(null, R.drawable.bot_image, null, Type.Bot)
        listConst[49] = Item(null, R.drawable.box_one_bg, null, Type.None)
        listConst[55] = Item(null, R.drawable.box_one_bg, null, Type.None)
        listConst[56] = Item(null, R.drawable.box_one_bg, null, Type.None)
        listConst[57] = Item(null, R.drawable.box_one_bg, null, Type.None)
        listConst[59] = Item(null, R.drawable.person_image, null, Type.Person)
        listConst[61] = Item(null, R.drawable.box_one_bg, null, Type.None)
        listConst[62] = Item(null, R.drawable.box_one_bg, null, Type.None)

    }

    private fun shuffleList() {

        shuffleList = ArrayList<Item>()
        val flagCount = rand(1, 3)
        var stoneCount = 0

        when (levelCount) {
            1 -> {
                stoneCount = 0
            }
            2 -> {
                stoneCount = rand(1, 3)
            }
            3 -> {
                stoneCount = rand(3, 5)
            }
            else -> {
                rand(1, 5)
            }
        }

        for (i in 0 until flagCount) {
            shuffleList.add(Item(null, R.drawable.flag_image, null, Type.Flag))
        }
        for (i in 0 until stoneCount) {
            shuffleList.add(Item(null, R.drawable.stone_image, null, Type.Stone))
        }
        val m = 63 - (15 + flagCount + stoneCount)

        for (i in 0 until m) {
            shuffleList.add(Item(null, R.drawable.back_style_rec, null, Type.Empty))
        }

        shuffleList.shuffle()
    }

    fun rand(from: Int, to: Int): Int {
        return random.nextInt(to - from) + from
    }

    private fun gameOverDialog() {

        SoundGame.mediaPlayer.stop()
        SoundGameOver.mediaPlayer.start()
        vibrate()
        val dialog = AlertDialog.Builder(requireContext()).create()
        val dialogView =
            GameOverDialogBinding.inflate(LayoutInflater.from(requireContext()), null, false)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialogView.replay.setOnClickListener {
            SoundGameOver.mediaPlayer.stop()
            findNavController().navigate(R.id.action_trustGameScreen_self)
            dialog.cancel()
        }
        dialogView.levels.setOnClickListener {
            findNavController().navigate(R.id.action_trustGameScreen_to_levelScreen)
            dialog.cancel()
        }
        dialogView.mainMenu.setOnClickListener {
            findNavController().navigate(R.id.action_trustGameScreen_to_mainScreen)
            dialog.cancel()
        }
        dialog.setCancelable(false)
        dialog.setView(dialogView.root)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun winDialog() {

        levelCount++
        sharedPreferences.levelCount = levelCount
        SoundGame.mediaPlayer.stop()
        sharedPreferences.levelCount = levelCount
        val dialog = AlertDialog.Builder(requireContext()).create()
        val dialogView =
            WinDialogBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        SoundGame.mediaPlayer.stop()
        dialogView.score.text = getString(R.string.score) + score
        dialogView.replay.setOnClickListener {
            findNavController().navigate(R.id.action_trustGameScreen_self)
            dialog.cancel()
        }
        dialogView.nextLevel.setOnClickListener {

            if (sharedPreferences.levelCount!! > 3) {
                findNavController().navigate(R.id.action_trustGameScreen_to_levelScreen)
            } else if (sharedPreferences.levelCount!! <= 3) {
                findNavController().navigate(R.id.action_trustGameScreen_self)
            }
            dialog.cancel()
        }
        dialogView.mainMenu.setOnClickListener {
            findNavController().navigate(R.id.action_trustGameScreen_to_mainScreen)
            dialog.cancel()
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        dialog.setCancelable(false)
        dialog.setView(dialogView.root)
        dialog.show()
    }

    private fun vibrate() {
        val vibe = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibe?.vibrate(VibrationEffect.createOneShot(150, 100))
        } else {
            vibe?.vibrate(100)
        }

    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.stop();
        mediaPlayer?.release();
    }

    private fun bonusMethod() {
        when (sharedPreferences.bonusCount) {
            1 -> {
                binding.bonus1.visibility = View.VISIBLE
                binding.bonus2.visibility = View.GONE
                binding.bonus3.visibility = View.GONE
            }
            2 -> {
                binding.bonus1.visibility = View.VISIBLE
                binding.bonus2.visibility = View.VISIBLE
                binding.bonus3.visibility = View.GONE
            }
            3 -> {
                binding.bonus1.visibility = View.VISIBLE
                binding.bonus2.visibility = View.VISIBLE
                binding.bonus3.visibility = View.VISIBLE
            }
        }
        binding.bonus1.setOnClickListener {
            binding.bonus1.visibility = View.GONE
            bonusOne = true
            bonusCount--
            sharedPreferences.bonusCount = bonusCount
        }

        binding.bonus2.setOnClickListener {
            binding.bonus2.visibility = View.GONE
            bonusTwo = true
            bonusCount--
            sharedPreferences.bonusCount = bonusCount
        }
        binding.bonus3.setOnClickListener {
            binding.bonus3.visibility = View.GONE
            bonusThree = true
            bonusCount--
            sharedPreferences.bonusCount = bonusCount
        }
    }

}